package com.techwarsol.bscs_7c

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.techwarsol.bscs_7c.Model.User
import com.techwarsol.bscs_7c.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegistrationBinding

    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        setDOBPicker()
        setSpinnerData()

        binding.txtMoveLogin.setOnClickListener {
            val intent =
                Intent(this@RegistrationActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUP.setOnClickListener {

            binding.pbLoader.visibility = View.VISIBLE
            binding.btnSignUP.visibility = View.GONE

            val userName = binding.etUserName.text.toString()
            val phone = binding.etPhone.text.toString()
            val email = binding.etEmail.text.toString().trim()
            val dob = binding.etDOB.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val spinnerData = binding.spSelectProvince.selectedItem.toString()


            if (userName.isEmpty()) {
                Toast.makeText(this@RegistrationActivity, "Enter You UserName", Toast.LENGTH_SHORT)
                    .show()

            } else if (phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this@RegistrationActivity,
                    "Enter All Fileds Data",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { results ->
                        if (results.isSuccessful) {

                            val uid = auth.uid
                            if (uid != null) {
                                database.reference.child("User").child(uid)
                                    .setValue(
                                        User(
                                            userName,
                                            email,
                                            password,
                                            dob,
                                            phone,
                                            spinnerData
                                        )
                                    )

                                    .addOnCompleteListener { task ->
                                        if(task.isSuccessful){

                                            binding.pbLoader.visibility = View.GONE
                                            binding.btnSignUP.visibility = View.VISIBLE

                                            val intent =
                                                Intent(this@RegistrationActivity, LoginActivity::class.java)
                                            intent.putExtra("welcomeText", "Welcome Mr. ${userName}")
                                            startActivity(intent)
                                            Toast.makeText(
                                                this@RegistrationActivity,
                                                "SignUp Success",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            binding.txtRegistration.text = " ${password}"

                                        }else{

                                            Toast.makeText(
                                                this@RegistrationActivity,
                                                "Adding User Data Failed",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }
                                    }

                            }



                        } else {
                            Toast.makeText(
                                this@RegistrationActivity,
                                "SignUp Fail",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    .addOnFailureListener { exception ->
                        binding.txtRegistration.text = exception.toString()
                        Toast.makeText(
                            this@RegistrationActivity,
                            exception.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun setSpinnerData() {
        val provinces = arrayOf("Select Province", "Punjab", "NWFP", "KPK", "Islamabad")

        val provinceAdapter = ArrayAdapter(
            this@RegistrationActivity,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, provinces
        )

        provinceAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        binding.spSelectProvince.adapter = provinceAdapter
    }

    private fun setDOBPicker() {
        binding.etDOB.setOnClickListener {

            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val date = calendar.get(Calendar.DATE)

            val datePicker = DatePickerDialog(
                this, { _, selectedYear, selectedMonth, selectedDay ->
                    val date = "$selectedDay, $selectedMonth, $selectedYear"
                    binding.etDOB.setText(date)
                }, year, month, date
            )

            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }
    }
}
