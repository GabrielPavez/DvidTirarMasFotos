package com.devst.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        // Referencias a los componentes del layout
        TextView tvEmailPerfil = findViewById(R.id.tvEmailPerfil);
        Button btnCerrarPerfil = findViewById(R.id.btnCerrarPerfil);

        // Recibir el email enviado desde HomeActivity
        String email = getIntent().getStringExtra("email_usuario");

        // Mostrar el email en el TextView. Si es nulo, muestra un texto por defecto.
        if (email != null && !email.isEmpty()) {
            tvEmailPerfil.setText(email);
        } else {
            tvEmailPerfil.setText("Email no disponible");
        }

        // Configurar el botón para que cierre esta pantalla y vuelva a HomeActivity
        btnCerrarPerfil.setOnClickListener(v -> {
            finish(); // Cierra la actividad actual
        });
    }
}