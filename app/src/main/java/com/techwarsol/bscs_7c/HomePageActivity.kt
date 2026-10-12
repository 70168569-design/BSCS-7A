package com.techwarsol.bscs_7c

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.techwarsol.bscs_7c.Model.Product
import com.techwarsol.bscs_7c.databinding.ActivityHomePageBinding
import com.techwarsol.bscs_7c.databinding.ActivityRegistrationBinding

class HomePageActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomePageBinding
    lateinit var auth: FirebaseAuth

    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)


        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Signout Process
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Signout", Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(this@HomePageActivity , LoginActivity::class.java)
            startActivity(intent)
        }

        //Add Product Process
        binding.btnAddProduct.setOnClickListener {

            val name = binding.etProductName.text.toString().trim()
            val desc = binding.etProductDescription.text.toString().trim()
            val category = binding.etProductCategory.text.toString().trim()
            val priceStr = binding.etProductPrice.text.toString().trim()
            val stockStr = binding.etProductStock.text.toString().trim()

            val price = priceStr.toDoubleOrNull()
            val stock = stockStr.toIntOrNull()

            if(name.isEmpty() || desc.isEmpty() || category.isEmpty()){

                Toast.makeText(this , "Enter Name, Description and Category" , Toast.LENGTH_SHORT).show()

            }else if(price == null || stock == null){
                Toast.makeText(this , "Enter Price and Stock" , Toast.LENGTH_SHORT).show()

            }else{

                addProduct(name, desc , category , price , stock)

            }

        }

    }

    private fun addProduct(
        name: String,
        desc: String,
        category: String,
        price: Double,
        stock: Int
    ) {

        binding.pbProduct.visibility = View.VISIBLE
        binding.btnAddProduct.isClickable = false
        binding.btnAddProduct.isEnabled = false

        database = FirebaseDatabase.getInstance().getReference("Product")

        val productID = database.push().key
        if(productID != null){

            val product = Product(name , desc , category , price , stock)

            database.child(productID).setValue(product)
                .addOnSuccessListener {
                    Toast.makeText(this, "Product Added Successfully", Toast.LENGTH_SHORT)
                        .show()

                    binding.pbProduct.visibility = View.GONE
                    binding.etProductName.setText("")
                    binding.etProductDescription.setText("")
                    binding.etProductCategory.setText("")
                    binding.etProductPrice.setText("")
                    binding.etProductStock.setText("")

                }
                .addOnFailureListener {
                    binding.pbProduct.visibility = View.GONE
                    binding.btnAddProduct.isClickable = true
                    binding.btnAddProduct.isEnabled = true


                    Toast.makeText(this, "Product Added Failed", Toast.LENGTH_SHORT)
                        .show()
                }

        }

    }
}