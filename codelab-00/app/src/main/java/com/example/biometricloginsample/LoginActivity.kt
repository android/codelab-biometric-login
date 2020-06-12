/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.biometricloginsample

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.example.biometricloginsample.databinding.ActivityLoginBinding

/**
 * After entering "valid" username and password, login button becomes enabled
 */
class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"

    private lateinit var binding: ActivityLoginBinding
    private val loginWithPasswordViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupForLoginWithPassword()
    }

    private fun setupForLoginWithPassword() {
        loginWithPasswordViewModel.loginWithPasswordFormState.observe(this, Observer { formState ->
            val loginState = formState ?: return@Observer
            when (loginState) {
                is SuccessfulLoginFormState -> binding.login.isEnabled = loginState.isDataValid
                is FailedLoginFormState -> {
                    loginState.usernameError?.let { binding.username.error = getString(it) }
                    loginState.passwordError?.let { binding.password.error = getString(it) }
                }
            }
        })
        loginWithPasswordViewModel.loginResult.observe(this, Observer {
            val loginResult = it ?: return@Observer
            if (loginResult.success) {
                updateApp(
                    "You successfully signed up using password as: user " +
                            "${SampleAppUser.username} with fake token ${SampleAppUser.fakeToken}"
                )
            }
        })
        binding.username.doAfterTextChanged {
            loginWithPasswordViewModel.onLoginDataChanged(
                binding.username.text.toString(),
                binding.password.text.toString()
            )
        }
        binding.password.doAfterTextChanged {
            loginWithPasswordViewModel.onLoginDataChanged(
                binding.username.text.toString(),
                binding.password.text.toString()
            )
        }
        binding.password.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE ->
                    loginWithPasswordViewModel.login(
                        binding.username.text.toString(),
                        binding.password.text.toString()
                    )
            }
            false
        }
        binding.login.setOnClickListener {
            loginWithPasswordViewModel.login(
                binding.username.text.toString(),
                binding.password.text.toString()
            )
        }
        Log.d(TAG, "Username ${SampleAppUser.username}; fake token ${SampleAppUser.fakeToken}")
    }

    private fun updateApp(successMsg: String) {
        binding.success.text = successMsg
    }
}