package au.edu.swin.sdmd.Core3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.firebase.storage.taskState

class EditProductActivity : AppCompatActivity() {
    // all views in teh activity
    lateinit var name : EditText
    lateinit var brand : EditText
    lateinit var stock : EditText
    lateinit var price : EditText
    lateinit var description : EditText
    lateinit var bottomAppBar: BottomAppBar
    lateinit var ProductImage: ImageView
    // variable for image uploaded by user
    lateinit var imageUri: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.product_edit_page)

        // defining views
        name = findViewById<EditText>(R.id.ProductName)
        brand = findViewById<EditText>(R.id.Brand)
        stock = findViewById<EditText>(R.id.Stock)
        price = findViewById<EditText>(R.id.Price)
        description = findViewById<EditText>(R.id.Description)
        bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        ProductImage = findViewById<ImageView>(R.id.ProductImage)

        // base imageview drawable
        ProductImage.setImageDrawable(this.getDrawable(R.drawable.baseline_add_24))

        // checking intent to know if product info is needed to be loaded
        val productName = intent.getStringExtra("name").toString()
        for (currentProduct in ProductStore.productList)
        {
            // if the product exist load data
            if(currentProduct.name == productName)
            {
                name.setText(currentProduct.name.toString())
                brand.setText(currentProduct.brand.toString())
                stock.setText(currentProduct.stock.toString())
                price.setText(currentProduct.price.toString())
                description.setText(currentProduct.description.toString())

                // image path if exist
                var imageRef = ProductStore.storageRef.child("images/${currentProduct.name.toString()}.jpg")

                // attempt to load image into imageView
                imageRef.downloadUrl.addOnSuccessListener {
                    // Local temp file has been created

                    Glide.with(this)
                        .load(it)
                        .error(R.drawable.baseline_delete_24)
                        .into(ProductImage)
                    Log.v("Image Load", "success load: images/${currentProduct.name.toString()}.jpg")
                }.addOnFailureListener {
                    // Handle any errors

                    ProductImage.setImageDrawable(this.getDrawable(android.R.drawable.ic_dialog_info))
                    Log.e("Image Load", "failed load: images/${currentProduct.name.toString()}.jpg")
                }
                break
            }
        }
        // allows user to upload picture to imageView
        ProductImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                chosePicture()
            }
        })

        // app bar to confirm creation or change of product
        bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_create -> {
                    try {
                        var newProduct = Product(
                            name.text.toString(),
                            brand.text.toString(),
                            Integer.parseInt(price.text.toString()),
                            Integer.parseInt(stock.text.toString()),
                            description.text.toString(),
                        )

                        // checking if a new product is attempted to be made
                        if(intent.getStringExtra("Activity").toString() == "main")
                        {
                            for (product in ProductStore.productList)
                            {
                                // checking if product already exists
                                if (product.name.toString() == name.text.toString())
                                {
                                    Toast.makeText(this, "Product Already Exist", Toast.LENGTH_SHORT).show()
                                    throw Exception("Already exist")
                                    break
                                }
                            }
                        }

                        // creating product and uploading image
                        ProductStore.writeNewPost(newProduct)
                        Log.w("created item", "tried")
                        uploadPicture()
                    } catch (e: Exception) {
                        // Do something to handle the error;
                        Log.w("created item", "failed")
                    }


                    true
                }
                else -> false
            }
        }
    }

    // choosing picture
    private fun chosePicture() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)

    }

    // getting image uploaded
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            ProductImage.setImageURI(imageUri)
        }
    }

    // uploading image to Firebase Storage
    private fun uploadPicture() {
        // checking if image exist or not
        val nameRef = ProductStore.storageRef.child("images/${name.text}.jpg")
        if(!this::imageUri.isInitialized)
        {
            // if not just skip
            startActivity(Intent(this@EditProductActivity, MainActivity::class.java))
            return
        }

        var uploadTask = nameRef.putFile(imageUri)

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Log.e("Image Upload", "failed")
        }.addOnSuccessListener {
            Log.v("Image Upload", "success")
            Toast.makeText(this, "Created ${name.text}", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@EditProductActivity, MainActivity::class.java))
        }
    }
}
