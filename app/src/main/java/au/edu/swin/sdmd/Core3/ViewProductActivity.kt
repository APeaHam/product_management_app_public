package au.edu.swin.sdmd.Core3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomappbar.BottomAppBar

class ViewProductActivity : AppCompatActivity() {
    // all views in the Activity
    lateinit var name : TextView
    lateinit var brand : TextView
    lateinit var stock : TextView
    lateinit var price : TextView
    lateinit var description : TextView
    lateinit var bottomAppBar: BottomAppBar
    lateinit var ProductImage: ImageView
    // product that will be displayed.
    lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.product_page_layout)

        // defining view
        name = findViewById<TextView>(R.id.ProductName)
        brand = findViewById<TextView>(R.id.Brand)
        stock = findViewById<TextView>(R.id.Stock)
        price = findViewById<TextView>(R.id.Price)
        description = findViewById<TextView>(R.id.Description)
        bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        ProductImage = findViewById<ImageView>(R.id.ProductImage)

        // finding product in productList
        val productName = intent.getStringExtra("name").toString()
        for (currentProduct in ProductStore.productList)
        {
            if(currentProduct.name == productName)
            {
                product = currentProduct
                break
            }
        }

        // checking if product was defined
        if(this::product.isInitialized)
        {
            name.setText(product.name.toString())
            brand.setText(product.brand.toString())
            stock.setText(product.stock.toString())
            description.setText(product.description.toString())
            price.setText(product.price.toString())
        }

        // reference to image in Firebase Storage
        var imageRef = ProductStore.storageRef.child("images/${product.name.toString()}.jpg")

        // load image into imageView
        imageRef.downloadUrl.addOnSuccessListener {
            // Local temp file has been created

            Glide.with(this)
                .load(it)
                .error(R.drawable.baseline_delete_24)
                .into(ProductImage)
            Log.v("Image Load", "success load: images/${product.name.toString()}.jpg")
        }.addOnFailureListener {
            // Handle any errors

            ProductImage.setImageDrawable(this.getDrawable(android.R.drawable.ic_dialog_info))
            Log.e("Image Load", "failed load: images/${product.name.toString()}.jpg")
        }

        // defining app bar usage
        bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                // send to edit page
                R.id.action_edit -> {
                    val intent = Intent(this, EditProductActivity::class.java)
                    intent.putExtra("name", product.name.toString())
                    this.startActivity(intent)


                    true
                }
                // send to delete product and return to main activity.
                R.id.action_delete -> {
                    try {
                        ProductStore.removePost(product)
                        Log.w("deleted item", "tried")
                    } catch (e: Exception) {
                        // Do something to handle the error;
                        Log.w("deleted item", "failed")
                    }
                    Toast.makeText(this, "Deleted ${product.name.toString()}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}