package ie.wit.umarketplace.ui.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.wit.umarketplace.R
import ie.wit.umarketplace.adapters.ProductAdapter
import ie.wit.umarketplace.adapters.ProductClickListener
import ie.wit.umarketplace.databinding.FragmentListBinding
import ie.wit.umarketplace.models.ProductModel
import ie.wit.umarketplace.ui.auth.LoggedInViewModel
import ie.wit.umarketplace.utils.*
import timber.log.Timber

class ListFragment : Fragment(), ProductClickListener {

    private var _fragBinding: FragmentListBinding? = null
    private val fragBinding get() = _fragBinding!!
    lateinit var loader : AlertDialog
    private val listViewModel: ListViewModel by activityViewModels()
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private var userProducts: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        loader = createLoader(requireActivity())

        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        fragBinding.fab.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToEditFragment(null)
            findNavController().navigate(action)
        }
        showLoader(loader, "Downloading Products")
        listViewModel.observableProductList.observe(viewLifecycleOwner, { products ->
            products?.let {
                render(products as ArrayList<ProductModel>)
                hideLoader(loader)
                checkSwipeRefresh()
            }
        })


        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val product = viewHolder.itemView.tag as ProductModel
                if(listViewModel.liveFirebaseUser.value?.email == product.email){
                    showLoader(loader,"Deleting Product")
                    val adapter = fragBinding.recyclerView.adapter as ProductAdapter
                    adapter.removeAt(viewHolder.adapterPosition)
                    listViewModel.delete(listViewModel.liveFirebaseUser.value?.uid!!, product.uid!!)
                    hideLoader(loader)
                }else{
                    if(userProducts){
                        listViewModel.load()
                    }else{
                        listViewModel.loadAll()
                    }
                }
            }
        }

        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragBinding.recyclerView)


        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val product = viewHolder.itemView.tag as ProductModel
                if(listViewModel.liveFirebaseUser.value?.email == product.email) {
                    Timber.i("product.uid == ${product.uid}")
                    val action =
                        ListFragmentDirections.actionListFragmentToEditFragment(product.uid!!)
                    findNavController().navigate(action)
                }else{
                    if(userProducts){
                        listViewModel.load()
                    }else{
                        listViewModel.loadAll()
                    }
                }
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(fragBinding.recyclerView)

        return root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)

        val item = menu.findItem(R.id.toggleUserProducts) as MenuItem
        item.setActionView(R.layout.togglebutton_layout)
        val toggleDonations: SwitchCompat = item.actionView.findViewById(R.id.toggleButton)
        toggleDonations.isChecked = false

        toggleDonations.setOnCheckedChangeListener { buttonView, isChecked ->
            userProducts = !isChecked
            Timber.i("isChecked == $isChecked")
            if (isChecked) listViewModel.loadAll()
            else listViewModel.load()
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
                NavigationUI.onNavDestinationSelected(item,
                    requireView().findNavController()) || super.onOptionsItemSelected(item)
        return true
    }

    private fun render(productList: ArrayList<ProductModel>) {
        fragBinding.recyclerView.adapter = ProductAdapter(productList,this)
        if (productList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.productsNotFound.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.productsNotFound.visibility = View.GONE
        }
    }

    private fun setSwipeRefresh() {
        fragBinding.swiperefresh.setOnRefreshListener {
            fragBinding.swiperefresh.isRefreshing = true
            showLoader(loader,"Downloading Donations")
            listViewModel.load()
        }
    }

    private fun checkSwipeRefresh() {
        if (fragBinding.swiperefresh.isRefreshing)
            fragBinding.swiperefresh.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        showLoader(loader,"Downloading Products")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if (firebaseUser != null) {
                listViewModel.liveFirebaseUser.value = firebaseUser
                listViewModel.load()
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onProductClick(product: ProductModel) {
        Timber.i("product.uid == ${product.uid}")
        val action = ListFragmentDirections.actionListFragmentToViewFragment(product.uid!!)
        findNavController().navigate(action)
    }
}

