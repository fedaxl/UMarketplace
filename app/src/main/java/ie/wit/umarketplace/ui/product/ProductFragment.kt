package ie.wit.umarketplace.ui.product

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import ie.wit.umarketplace.R
import ie.wit.umarketplace.databinding.FragmentProductBinding
import ie.wit.umarketplace.models.ProductModel
import ie.wit.umarketplace.ui.report.ReportViewModel

class ProductFragment : Fragment() {

    var totalAdded = 0
    private var _fragBinding: FragmentProductBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val fragBinding get() = _fragBinding!!
    private lateinit var productViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _fragBinding = FragmentProductBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        productViewModel.observableStatus.observe(viewLifecycleOwner, Observer {
                status -> status?.let { render(status) }
        })

        fragBinding.progressBar.max = 10000
        fragBinding.amountPicker.minValue = 1
        fragBinding.amountPicker.maxValue = 1000

        fragBinding.amountPicker.setOnValueChangedListener { _, _, newVal ->
            //Display the newly selected number to paymentAmount
            fragBinding.paymentAmount.setText("$newVal")
        }
        setButtonListener(fragBinding)
        return root;
    }

    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {
                    //Uncomment this if you want to immediately return to Report
                    //findNavController().popBackStack()
                }
            }
            false -> Toast.makeText(context,getString(R.string.productError),Toast.LENGTH_LONG).show()
        }
    }

    fun setButtonListener(layout: FragmentProductBinding) {
        layout.addButton.setOnClickListener {
            val amount = if (layout.paymentAmount.text.isNotEmpty())
                layout.paymentAmount.text.toString().toInt() else layout.amountPicker.value
            if(totalAdded >= layout.progressBar.max)
                Toast.makeText(context,"Selling Amount Exceeded!", Toast.LENGTH_LONG).show()
            else {
                val paymentmethod = if(layout.paymentMethod.checkedRadioButtonId == R.id.Direct) "Direct" else "Paypal"
                totalAdded += amount
                layout.totalSoFar.text = "$$totalAdded"
                layout.progressBar.progress = totalAdded
                productViewModel.addProduct(ProductModel(paymentmethod = paymentmethod,amount = amount))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_product, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
                requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()
        val reportViewModel = ViewModelProvider(this).get(ReportViewModel::class.java)
        reportViewModel.observableProductsList.observe(viewLifecycleOwner, Observer {
                totalAdded = reportViewModel.observableProductsList.value!!.sumOf { it.amount }
                fragBinding.progressBar.progress = totalAdded
                fragBinding.totalSoFar.text = String.format(getString(R.string.totalSoFar),totalAdded)
        })
    }
}