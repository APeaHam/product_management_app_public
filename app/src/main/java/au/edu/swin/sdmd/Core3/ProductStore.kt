package au.edu.swin.sdmd.Core3

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.firebase.storage.storage

object ProductStore {

    // reference to Firebase Realtime Database
    val database = Firebase.database.getReference("products")
    // arraylist of all products
    var productList =  ArrayList<Product>()
    // arraylist of products to be displayed
    var displayList =  ArrayList<Product>()
    // reference to Firebase Storage
    val storageRef = Firebase.storage.reference

    // used to create new product
    fun writeNewPost(newProduct : Product) {
        // Create new post at /products/$newProduct.name
        val key = newProduct.name
        if (key == null) {
            Log.w(TAG, "Couldn't get push key for posts")
            return
        }


        val postValues = newProduct.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "$key" to postValues
        )

        database.updateChildren(childUpdates)
    }

    // used to remove a product
    fun removePost(deleteProduct : Product) {
        // call remove of product in FireBase Realtime Database
        database.child(deleteProduct.name.toString()).removeValue()
        // Delete the file
        storageRef.child("images/${deleteProduct.name.toString()}.jpg").delete().addOnSuccessListener {
            // File deleted successfully
            Log.v("Deleted Image", "success delete: images/${deleteProduct.name.toString()}.jpg")
        }.addOnFailureListener {
            // Uh-oh, an error occurred!
            Log.e("Deleted Image", "failed delete: images/${deleteProduct.name.toString()}.jpg")
        }
    }
}