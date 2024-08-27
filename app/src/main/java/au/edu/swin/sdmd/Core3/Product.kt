package au.edu.swin.sdmd.Core3

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

// data class used for product
@IgnoreExtraProperties
data class Product(
    var name: String? = null,
    var brand: String? = null,
    var price: Int = 0,
    var stock: Int = 0,
    var description: String? = null,
) {

    // maping used to upload and read data from Firebase Realtime Database
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "brand" to brand,
            "price" to price,
            "stock" to stock,
            "description" to description,
        )
    }
}
