package ie.wit.umarketplace.ui.report

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.wit.umarketplace.R
import ie.wit.umarketplace.adapters.ProductAdapter
import ie.wit.umarketplace.adapters.ProductClickListener
import ie.wit.umarketplace.databinding.FragmentReportBinding
import ie.wit.umarketplace.main.UMarketplaceApp
import ie.wit.umarketplace.models.ProductModel
import ie.wit.umarketplace.utils.SwipeToDeleteCallback
import ie.wit.umarketplace.utils.createLoader
import ie.wit.umarketplace.utils.hideLoader
import ie.wit.umarketplace.utils.showLoader

class ReportFragment : Fragment(), ProductClickListener {

    lateinit var app: UMarketplaceApp
    private var _fragBinding: FragmentReportBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var reportViewModel: ReportViewModel
    lateinit var loader : AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentReportBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        loader = createLoader(requireActivity())

        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        reportViewModel = ViewModelProvider(this).get(ReportViewModel::class.java)
        showLoader(loader,"Downloading Products")
        reportViewModel.observableProductsList.observe(viewLifecycleOwner, Observer {
                products ->
            products?.let {
                render(products as ArrayList<ProductModel>)
                hideLoader(loader)
                checkSwipeRefresh()
            }
        })

        fragBinding.fab.setOnClickListener {
            val action = ReportFragmentDirections.actionReportFragmentToProductFragment()
            findNavController().navigate(action)
        }

        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showLoader(loader,"Deleting Product")
                val adapter = fragBinding.recyclerView.adapter as ProductAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                reportViewModel.delete(viewHolder.itemView.tag as String)
                hideLoader(loader)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragBinding.recyclerView)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_report, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
                requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    private fun render(productsList: ArrayList<ProductModel>) {
        fragBinding.recyclerView.adapter = ProductAdapter(productsList,this)
        if (productsList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.productsNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.productsNotFound.visibility = View.GONE
        }
    }

    override fun onProductClick(product: ProductModel) {
        val action = ReportFragmentDirections.actionReportFragmentToProductDetailFragment(product._id)
        findNavController().navigate(action)
    }

    fun setSwipeRefresh() {
        fragBinding.swiperefresh.setOnRefreshListener {
            fragBinding.swiperefresh.isRefreshing = true
            showLoader(loader,"Downloading Products")
            reportViewModel.load()
        }
    }

    fun checkSwipeRefresh() {
        if (fragBinding.swiperefresh.isRefreshing)
            fragBinding.swiperefresh.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        reportViewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}