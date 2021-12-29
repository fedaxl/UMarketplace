package ie.wit.umarketplace.ui.edit

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.squareup.picasso.Picasso
import ie.wit.umarketplace.R
import ie.wit.umarketplace.databinding.FragmentEditBinding
import ie.wit.umarketplace.helpers.checkLocationPermissions
import ie.wit.umarketplace.helpers.createDefaultLocationRequest
import ie.wit.umarketplace.models.Location
import ie.wit.umarketplace.models.ProductModel
import ie.wit.umarketplace.ui.auth.LoggedInViewModel
import ie.wit.umarketplace.ui.editlocation.EditLocationActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber


class EditFragment : Fragment(), OnMapReadyCallback {

    private var _fragBinding: FragmentEditBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val args by navArgs<EditFragmentArgs>()
    private val fragBinding get() = _fragBinding!!
    private lateinit var editViewModel: EditViewModel
    private lateinit var imageIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private lateinit var locationService : FusedLocationProviderClient
    private val locationRequest = createDefaultLocationRequest()
    var edit: Boolean = false
    var mapReady : Boolean = false
    var locationReady : Boolean = false
    var locationEdited : Boolean = false
    var imageChanged : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        locationService = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        registerImagePickerCallback()
        registerMapCallback()
        doPermissionLauncher()

        _fragBinding = FragmentEditBinding.inflate(inflater, container, false)
        val root = fragBinding.root


        editViewModel = ViewModelProvider(this).get(EditViewModel::class.java)

        editViewModel.observableProduct.observe(viewLifecycleOwner, {
            renderProduct(it)
        })

        editViewModel.observableStatus.observe(viewLifecycleOwner, {
                status -> status?.let { render(status) }
        })

        edit = args.productid != null && args.productid != "null"
        if(edit){
            Timber.i("EDIT ${args.productid}")
            editViewModel.getProduct(loggedInViewModel.liveFirebaseUser.value?.uid!!, args.productid)
        }else{
            Timber.i("NEW")
            editViewModel.setDefaultProduct(loggedInViewModel.liveFirebaseUser.value?.uid!!)
            if (checkLocationPermissions(requireActivity())) {
                doSetCurrentLocation()
            }
        }

        return root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragBinding.mapView.onCreate(savedInstanceState);
        fragBinding.mapView.onResume();
        fragBinding.mapView.getMapAsync(this)
    }

    override fun onMapReady(m: GoogleMap) {
        Timber.i("ON MAP READY")
        editViewModel.doConfigureMap(m)
        mapReady = true
        locationUpdate()

    }
    private fun locationUpdate(){
        if(mapReady && locationReady){
            Timber.i("MAP AND LOCATION READY $mapReady $locationReady")
            editViewModel.mapLocationUpdate()
        }
    }
    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {
                    //comment this if you want to immediately return to Report
                    findNavController().popBackStack()
                }
            }
            false -> Toast.makeText(context,getString(R.string.productError),Toast.LENGTH_LONG).show()
        }
    }

    private fun renderProduct(product:ProductModel) {
        fragBinding.productvm = editViewModel

        if(edit){
            Timber.i("ON LOCATION READY")
            locationReady = true
            locationUpdate()
            if(product.image != ""){
                editViewModel.loadImage(product.image, fragBinding.productImage)
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Ref. https://stackoverflow.com/questions/38195522/what-is-oncreateoptionsmenumenu-menu
        return when (item.itemId) {
            R.id.listFragment -> {
                NavigationUI.onNavDestinationSelected(item,
                    requireView().findNavController()) || super.onOptionsItemSelected(item)
            }
            R.id.mapFragment -> {
                NavigationUI.onNavDestinationSelected(item,
                    requireView().findNavController()) || super.onOptionsItemSelected(item)
            }
            R.id.save -> {
                if(edit){
                    Timber.i("EDIT PRODUCT ${fragBinding.productvm?.observableProduct!!.value!!}")
                    editViewModel.editProduct(loggedInViewModel.liveFirebaseUser.value?.uid!!, args.productid!!, fragBinding.productvm?.observableProduct!!.value!!, requireContext(), imageChanged)
                }else{
                    editViewModel.editProduct(
                        loggedInViewModel.liveFirebaseUser.toString(), args.productid!!, fragBinding.productvm?.observableProduct!!.value!!,
                        requireContext(), imageChanged)
                }
                true
            }

            R.id.location -> {
                var location = editViewModel.getLocation()
                val launcherIntent = Intent(activity, EditLocationActivity::class.java).putExtra("location", location)
                mapIntentLauncher.launch(launcherIntent)
                true
            }
            R.id.image -> {
                editViewModel.doSelectImage(imageIntentLauncher)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

   override fun onResume() {
        super.onResume()
        //doRestartLocationUpdates()
    }



    @SuppressLint("MissingPermission")
    fun doSetCurrentLocation() {
        locationService.lastLocation.addOnSuccessListener {
            val location = Location()
            location.lat = it.latitude
            location.lng = it.longitude
            location.zoom = 15f
            Timber.i("CURRENT LOCATION $location")
            editViewModel.setProductLocation(location)
            Timber.i("LOCATION READY")
            locationReady = true
            locationUpdate()
        }
    }

    private fun doPermissionLauncher() {
        requestPermissionLauncher =
            this.registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    doSetCurrentLocation()
                } else {
                    val location = Location()
                    location.lat = 40.0
                    location.lng = -10.0
                    location.zoom = 15f
                    editViewModel.setProductLocation(location)
                    Timber.i("LOCATION READY")
                    locationReady = true
                    locationUpdate()
                }
            }
    }

   /*@SuppressLint("MissingPermission")
   //\ https://next.tutors.dev/#/lab/wit-hdip-comp-sci-2020-mobile-app-dev.netlify.app/topic-10-location/unit-03-dh/book-03-location-tracking/Exercises
    fun doRestartLocationUpdates() {
        var locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null && !locationEdited) {
                    val l = locationResult.locations.last()
                    val location = Location()
                    location.lat = l.latitude
                    location.lng = l.longitude
                    location.zoom = 15f
                    editViewModel.setProductLocation(location)
                    Timber.i("LOCATION READY")
                    locationReady = true
                    locationUpdate()
                }
            }
        }
        if (!edit) {
            locationService.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }*/

    private fun registerImagePickerCallback() {

        imageIntentLauncher =
            this.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when(result.resultCode){
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            Timber.i("Got Image Result ${result.data!!.data.toString()}")
                            editViewModel.setImage(result.data!!.data.toString())
                            imageChanged = true
                            Picasso.get()
                                .load(result.data!!.data!!.toString())
                                .resize(200, 200)
                                .into(fragBinding.productImage)
                        }
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }

            }
    }
    private fun registerMapCallback() {
        mapIntentLauncher =
            this.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            Timber.i("Location EXTRA $location")
                            editViewModel.setProductLocation(location)
                            Timber.i("LOCATION READY")
                            locationEdited = true
                            locationReady = true
                            locationUpdate()
                        } // end of if
                    }
                    AppCompatActivity.RESULT_CANCELED -> { } else -> { }
                }

            }
    }


}

