package com.devst.app;

// Se cargan las herramientas necesarias de Android para la pantalla.
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

// Esta es la pantalla (Activity) que muestra la sección de ayuda.
public class AyudaActivity extends AppCompatActivity {

    // Se ejecuta cuando se crea la pantalla. Aquí se configura el diseño y los eventos.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ayuda);

        // Se enlaza cada variable con su respectivo elemento en el diseño XML.
        Button btnVolverHome = findViewById(R.id.btnVolverHome);
        Toolbar toolbar = findViewById(R.id.toolbar_ayuda);

        // Se establece la barra de herramientas y se le añade una flecha para volver atrás.
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Se define lo que hará el botón "Volver al Inicio" cuando se presione.
        btnVolverHome.setOnClickListener(v -> {
            finish(); // Cierra la pantalla actual y regresa a la anterior (HomeActivity).
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}