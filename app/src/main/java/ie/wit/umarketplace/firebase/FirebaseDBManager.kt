package ie.wit.umarketplace.firebase


import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import ie.wit.umarketplace.helpers.readImageFromPath
import ie.wit.umarketplace.models.ProductModel
import ie.wit.umarketplace.models.ProductStore
import timber.log.Timber
import java.io.ByteArrayOutputStream


object FirebaseDBManager: ProductStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    var st: StorageReference = FirebaseStorage.getInstance().reference

    override fun findAll(productsList: MutableLiveData<List<ProductModel>>) {
        database.child("products")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Product error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<ProductModel>()
                    val children = snapshot.children
                    children.forEach {
                        val product = it.getValue(ProductModel::class.java)
                        localList.add(product!!)
                    }
                    database.child("products")
                        .removeEventListener(this)

                    productsList.value = localList
                }
            })
    }

    override fun findAll(email: String, productsList: MutableLiveData<List<ProductModel>>) {

            Timber.i("Find all by user : $email")
                database.child("products")
            .addValueEventListener(object : ValueEventListener {

                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase product error : ${error.message}")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<ProductModel>()
                    val children = snapshot.children
                    children.forEach {
                        val product = it.getValue(ProductModel::class.java)
                        if(product!!.email == email){
                            localList.add(product)
                        }
                    }
                    database.child("products")
                        .removeEventListener(this)

                    productsList.value = localList
                }
            })

    }

    override fun findById(productid: String, product: MutableLiveData<ProductModel>) {

                database.child("products").child(productid).get()
                    .addOnSuccessListener {
                        product.value = it.getValue(ProductModel::class.java)
                Timber.i("firebase Got value ${it.value}")
            }.addOnFailureListener{
                Timber.e("firebase Error getting data $it")
            }

    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, product: ProductModel, context: Context) {
        Timber.i("Firebase DB Reference : $database")
        val uid = firebaseUser.value!!.uid
        val key = database.child("products").push().key
        Timber.i("Firebase DB create key : $key")
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        Timber.i("IMAGE STRING CREATE : ${product.image}")
        product.uid = key
        database.child("products").child(key).setValue(product)
        updateImage(product.image, uid, key, context)
    }

    override fun delete(userid: String, productid: String) {

        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/products/$productid"] = null

        database.updateChildren(childDelete)
    }

    override fun update(userid: String, productid: String, product: ProductModel, context: Context, imageChanged: Boolean) {
        database.child("products").child(productid).setValue(product)
        if(imageChanged){
            updateImage(product.image, userid, productid, context)
        }
    }

    private fun updateImage(imageStr: String, uid:String, key: String, context: Context){

        Timber.i("IMAGE STRING UPDATEIMAGE : ${imageStr}")
        if(imageStr != ""){
            var imageRef = st.child("$uid/$key")
            val baos = ByteArrayOutputStream()
            val bitmap = readImageFromPath(context, imageStr)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val data = baos.toByteArray()
            val uploadTask = imageRef.putBytes(data)
            Timber.i("updateImage before listener")
            uploadTask.addOnSuccessListener { taskSnapshot ->
                Timber.i("updateImage uploaded")
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    Timber.i("updateImage downloadUrl")
                    var imageURL = it.toString()
                    Timber.i("IMAGE URL UPDATEIMAGE : $imageURL")
                    database.child("products").child(key).child("image").setValue(imageURL)

                }
            }.addOnFailureListener{
                var errorMessage = it.message
                Timber.i("Failure: $errorMessage")
            }

        }

    }

}