package au.edu.swin.sdmd.Core3

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class ProductListAdapter: RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {
    lateinit var parent: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.product_list_layout, parent, false) as View
        this.parent = parent
        return ViewHolder(view)
    }

    // displayList is used instead of ProductList.
    override fun getItemCount(): Int = ProductStore.displayList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ProductStore.displayList[position]

        holder.bind(item)
    }

    inner class ViewHolder(val v: View): RecyclerView.ViewHolder(v) {
        // views in viewHolder
        val image = v.findViewById<ImageView>(R.id.ProductImage)
        val product = v.findViewById<TextView>(R.id.ProductName)
        val brand = v.findViewById<TextView>(R.id.BrandList)
        val stock = v.findViewById<TextView>(R.id.StockList)
        val price = v.findViewById<TextView>(R.id.PriceList)
        fun bind(item: Product) {
            // setting product information
            product.setText(item.name)
            brand.setText("${this@ProductListAdapter.parent.context.getString(R.string.brand_title)} ${item.brand}")
            stock.setText("${this@ProductListAdapter.parent.context.getString(R.string.stock_title)} ${item.stock.toString()}")
            price.setText("${this@ProductListAdapter.parent.context.getString(R.string.price_title)} ${item.price.toString()}")

            // image path in database
            var imageRef = ProductStore.storageRef.child("images/${item.name.toString()}.jpg")

            // loading image into imageView
            imageRef.downloadUrl.addOnSuccessListener {
                // Local temp file has been created

                Glide.with(v)
                    .load(it)
                    .error(R.drawable.baseline_delete_24)
                    .into(image)
                Log.v("Image Load", "success load: images/${item.name.toString()}.jpg")
            }.addOnFailureListener {
                // Handle any errors

                image.setImageDrawable(parent.context.getDrawable(android.R.drawable.ic_dialog_info))
                Log.e("Image Load", "failed load: images/${item.name.toString()}.jpg")
            }

            // when view clicked the product info page is displayed.
            v.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    val intent = Intent(parent.context, ViewProductActivity::class.java)
                    intent.putExtra("name", item.name.toString())
                    parent.context.startActivity(intent)
                }

            })
        }

    }
}