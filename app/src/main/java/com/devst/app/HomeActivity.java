package com.devst.app;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

// Esta es la pantalla principal (Activity) que controla el menú de "tiles".
public class HomeActivity extends AppCompatActivity {

    // Variables que se necesitarán en diferentes partes de la clase.
    private String emailUsuario = "";
    private TextView tvLinterna;
    private CameraManager camara;
    private String camaraID = null;
    private boolean luz = false;
    private Uri imageUri;

    // Estos objetos se preparan para manejar respuestas de otras pantallas o diálogos,
    // como pedir permisos o esperar el resultado de la cámara.
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    lanzarCamara();
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
            });
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(this, "Foto guardada en la galería", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Captura cancelada", Toast.LENGTH_SHORT).show();
                }
            });

    // Se ejecuta al crear la pantalla. Aquí se configura el diseño y se asignan los eventos.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Permite que la app ocupe toda la pantalla.
        setContentView(R.layout.activity_home); // Conecta este código con el diseño XML.

        // Se configuran las referencias a los elementos visuales del diseño.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        View btnIrPerfil = findViewById(R.id.btnIrPerfil);
        View btnAbrirWeb = findViewById(R.id.btnAbrirWeb);
        View btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo);
        View btnCompartir = findViewById(R.id.btnCompartir);
        View btnLinterna = findViewById(R.id.btnLinterna);
        View btnCamara = findViewById(R.id.btnCamara);
        View btnAbrirMapa = findViewById(R.id.btnAbrirMapa);
        View btnLlamar = findViewById(R.id.btnLlamar);
        View btnAyuda = findViewById(R.id.btnAyuda);
        tvLinterna = findViewById(R.id.tv_linterna);

        // Se recibe información (el email) de la pantalla de Login.
        emailUsuario = getIntent().getStringExtra("email_usuario");
        if (emailUsuario == null) emailUsuario = "";

        // A cada "tile" se le asigna una acción para cuando el usuario haga clic.
        btnIrPerfil.setOnClickListener(v -> {
            Uri githubUri = Uri.parse("https://github.com/gabrielpavez");
            Intent intent = new Intent(Intent.ACTION_VIEW, githubUri);
            startActivity(intent);
        });
        btnAyuda.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, AyudaActivity.class);
            startActivity(i);
        });
        btnAbrirWeb.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.santotomas.cl");
            Intent viewWeb = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(viewWeb);
        });
        btnAbrirMapa.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=Instituto Profesional Santo Tomas");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, "No se encontró ninguna aplicación de mapas.", Toast.LENGTH_SHORT).show();
            }
        });
        btnEnviarCorreo.setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse("mailto:"));
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailUsuario});
            email.putExtra(Intent.EXTRA_SUBJECT, "Prueba desde la app");
            email.putExtra(Intent.EXTRA_TEXT, "Hola, esto es un intento de correo.");
            startActivity(Intent.createChooser(email, "Enviar correo con:"));
        });
        btnCompartir.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, "Hola desde mi app Android 😎");
            startActivity(Intent.createChooser(share, "Compartir usando:"));
        });
        btnLinterna.setOnClickListener(v -> {
            if (camaraID == null) {
                Toast.makeText(this, "Este dispositivo no tiene flash disponible", Toast.LENGTH_SHORT).show();
                return;
            }
            alternarluz();
        });
        btnCamara.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                lanzarCamara();
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });
        btnLlamar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:974576230"));
            startActivity(intent);
        });

        // Se inicializa el servicio de la cámara para poder usar la linterna.
        try {
            camara = (CameraManager) getSystemService(CAMERA_SERVICE);
            for (String id : camara.getCameraIdList()) {
                CameraCharacteristics cc = camara.getCameraCharacteristics(id);
                if (Boolean.TRUE.equals(cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE))
                        && cc.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    camaraID = id;
                    break;
                }
            }
        } catch (CameraAccessException e) {
            Toast.makeText(this, "No se puede acceder a la cámara", Toast.LENGTH_SHORT).show();
        }
    }

    // Prepara y lanza el Intent para abrir la cámara.
    private void lanzarCamara() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Nueva Foto");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Desde la App Prototipo");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraLauncher.launch(intent);
    }

    // Controla el encendido y apagado de la linterna.
    private void alternarluz() {
        try {
            luz = !luz;
            camara.setTorchMode(camaraID, luz);
            tvLinterna.setText(luz ? "Linterna (ON)" : "Linterna (OFF)");
        } catch (CameraAccessException e) {
            Toast.makeText(this, "Error al controlar la linterna", Toast.LENGTH_SHORT).show();
        }
    }

    // Se ejecuta cuando la app se pone en segundo plano. Útil para apagar la linterna.
    @Override
    protected void onPause() {
        super.onPause();
        if (camaraID != null && luz) {
            try {
                camara.setTorchMode(camaraID, false);
                luz = false;
                if (tvLinterna != null) tvLinterna.setText("Linterna");
            } catch (CameraAccessException ignored) {}
        }
    }

    // Se encargan de crear y manejar las opciones del menú de la barra de herramientas.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_perfil) {
            Intent i = new Intent(this, PerfilActivity.class);
            i.putExtra("email_usuario", emailUsuario);
            startActivity(i);
            return true;
        } else if (id == R.id.action_web) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com")));
            return true;
        } else if (id == R.id.action_salir) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}