package com.devst.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Patterns;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

// Esta es la pantalla (Activity) que controla la vista de inicio de sesión.
public class LoginActivity extends AppCompatActivity {

    // Variables para los campos de texto y el botón del diseño.
    private EditText edtEmail, edtPass;
    private Button btnLogin;
    
    // Se ejecuta al crear la pantalla. Aquí se configura el diseño y los eventos de clic.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Permite que la app ocupe toda la pantalla.
        setContentView(R.layout.activity_login); // Conecta este código con su diseño XML.

        // Se enlaza cada variable con su respectivo elemento en el diseño XML.
        edtEmail = findViewById(R.id.edtEmail);
        edtPass  = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);

        // Se define qué sucede cuando el usuario presiona los botones o textos.
        btnLogin.setOnClickListener(v -> intentoInicioSesion()); // Llama a la función de login.
        findViewById(R.id.tvRecuperarpass).setOnClickListener(v ->
                Toast.makeText(this, "Función pendiente: recuperar contraseña", Toast.LENGTH_SHORT).show());
        findViewById(R.id.tvCrear).setOnClickListener(v ->
                Toast.makeText(this, "Función pendiente: crear cuenta", Toast.LENGTH_SHORT).show());
    }

    // Contiene toda la lógica para validar los datos y permitir el acceso.
    private void intentoInicioSesion() {
        // Se obtienen los textos ingresados por el usuario.
        String email = edtEmail.getText().toString().trim();
        String pass  = edtPass.getText().toString();

        // Se comprueba que el correo y la contraseña no estén vacíos y cumplan los requisitos.
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Ingresa tu correo");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Correo inválido");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            edtPass.setError("Ingresa tu contraseña");
            return;
        }
        if (pass.length() < 6) {
            edtPass.setError("Mínimo 6 caracteres");
            return;
        }

        // Se comprueba si el correo y la contraseña son los correctos.
        boolean ok = email.equals("estudiante@st.cl") && pass.equals("123456");
        if (ok) {
            Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();

            // Se prepara y ejecuta la navegación a la pantalla principal (HomeActivity).
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("email_usuario", email);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        }
    }
}