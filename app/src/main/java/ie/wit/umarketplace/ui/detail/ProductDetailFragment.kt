package ie.wit.umarketplace.ui.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import ie.wit.umarketplace.databinding.FragmentProductDetailBinding
import timber.log.Timber


class ProductDetailFragment : Fragment() {

    private lateinit var detailViewModel: ProductDetailViewModel
    private val args by navArgs<ProductDetailFragmentArgs>()

    private var _fragBinding: FragmentProductDetailBinding? = null
    private val fragBinding get() = _fragBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentProductDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        detailViewModel = ViewModelProvider(this).get(ProductDetailViewModel::class.java)
        detailViewModel.observableProduct.observe(viewLifecycleOwner, Observer { render() })
        return root
    }

    private fun render() {
        fragBinding.editMessage.setText("A Message")
        fragBinding.editUpvotes.setText("0")
        fragBinding.productvm = detailViewModel
        Timber.i("Retrofit fragBinding.productvm == $fragBinding.productvm")
    }

    override fun onResume() {
        super.onResume()
        detailViewModel.getProduct(args.productid)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }
}