package au.edu.swin.sdmd.Core3

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue


class MainActivity : AppCompatActivity() {
    // references to list adaptor and searchbar
    lateinit var productList : ProductListAdapter
    lateinit var searchBar : SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // adding database child listener
        ProductStore.database.addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(ContentValues.TAG, "onChildAdded:" + dataSnapshot.key!!)
                // A new product has been added, add it to the displayed list
                val newProduct = dataSnapshot.getValue<Product>()
                // only add new products to the list
                if (newProduct?.name != null)
                {
                    if(!ProductStore.productList.contains(newProduct))
                    {
                        ProductStore.productList.add(newProduct)
                    }
                }
                // filter list if needed
                filter()
                productList.notifyDataSetChanged()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(ContentValues.TAG, "onChildChanged: ${dataSnapshot.key}")

                // A product has changed, use the key to determine if we are displaying this
                // product and if so displayed the changed comment.
                val newProduct = dataSnapshot.getValue<Product>()
                val productKey = dataSnapshot.key

                // finding product that has been changed
                var found = false
                var index = 0
                ProductStore.productList.forEach {
                    if(it.name == productKey) {
                        found = true
                        index = ProductStore.productList.indexOf(it)
                    }
                }

                // if change was found update information
                if (found && newProduct != null)
                {
                    ProductStore.productList[index] = newProduct
                }
                // filter list if needed
                filter()
                productList.notifyDataSetChanged()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(ContentValues.TAG, "onChildRemoved:" + dataSnapshot.key!!)

                // A product has changed, use the key to determine if we are displaying this
                // product and if so remove it.
                val productKey = dataSnapshot.key

                // finding child that was removed
                var found = false
                var index = 0
                ProductStore.productList.forEach {
                    if(it.name == productKey) {
                        found = true
                        index = ProductStore.productList.indexOf(it)
                    }
                }

                // if found remove from product list
                if (found)
                {
                    ProductStore.productList.remove(ProductStore.productList[index])
                }
                // filter list if needed
                filter()
                productList.notifyDataSetChanged()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "postComments:onCancelled", databaseError.toException())
            }
        })

        // setting up recyclerView
        val list = findViewById<RecyclerView>(R.id.ProductList)
        productList  = ProductListAdapter()

        list.adapter = productList
        list.layoutManager = LinearLayoutManager(this)

        val fabNewProduct = findViewById<View>(R.id.floatingActionButton)

        // adding fab action
        fabNewProduct.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                // sends user to create product page.
                val intent = Intent(this@MainActivity, EditProductActivity::class.java)
                intent.putExtra("Activity", "main")
                this@MainActivity.startActivity(intent)
            }

        })

        // searchbar setting
        searchBar = findViewById<SearchView>(R.id.searchBar)
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Override onQueryTextSubmit method which is call when submit query is searched
            override fun onQueryTextSubmit(query: String): Boolean {
                // If the list contains the search query than filter the adapter
                // using the filter method with the query as its argument
                filter()
                productList.notifyDataSetChanged()
                return false
            }

            // This method is overridden to filter the adapter according
            // to a search query when the user is typing search
            override fun onQueryTextChange(newText: String): Boolean {
                filter()
                productList.notifyDataSetChanged()
                return false
            }
        })

        productList.notifyDataSetChanged()
    }

    // filtering for products with the name containing the string in the searchbar
    fun filter()
    {
        ProductStore.displayList = ProductStore.productList
        if (searchBar.query.toString() != null)
        {
            ProductStore.displayList = ProductStore.productList.filter { it.name.toString().contains(searchBar.query)
                 } as ArrayList<Product>
        }
    }
}