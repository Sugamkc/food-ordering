@file:Suppress("PackageName")

package com.example.foodordering.Activity.Splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.foodordering.Activity.Auth.LoginActivity
import com.example.foodordering.Activity.Auth.SignUpActivity
import com.example.foodordering.Activity.BaseActivity
import com.example.foodordering.R

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen(
                onLogInClick = {
                    startActivity(Intent(this, LoginActivity::class.java))
                },
                onSignUpClick = {
                    startActivity(Intent(this, SignUpActivity::class.java))
                }
            )
        }
    }
}

@Composable
@Preview
fun SplashScreen(
    onLogInClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.darkBrown))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp)
        ) {
            val (backgroundImg, logoImg) = createRefs()

            Image(
                painter = painterResource(id = R.drawable.intro_pic),
                contentDescription = "Intro Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .constrainAs(backgroundImg) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                contentScale = ContentScale.Crop
            )

            Image(
                painter = painterResource(R.drawable.pizza),
                contentDescription = "Logo Image",
                modifier = Modifier
                    .size(100.dp)
                    .constrainAs(logoImg) {
                        top.linkTo(backgroundImg.top)
                        bottom.linkTo(backgroundImg.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val styledText = buildAnnotatedString {
            append("Food That Feels\n")
            withStyle(style = SpanStyle(color = colorResource(R.color.orange))) {
                append("Just Right...\n")
            }
        }

        Text(
            text = styledText,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Welcome Back\nGood to see you again. Continue your journey with a smooth, connected experience.",
            fontSize = 13.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        )

        GetStartButton(
            onLogInClick = onLogInClick,
            onSignUpClick = onSignUpClick,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}
