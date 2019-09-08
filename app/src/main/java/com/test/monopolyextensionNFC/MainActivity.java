package com.test.monopolyextensionNFC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.firebase.ui.auth.AuthUI;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;

import com.test.monopolyextensionNFC.parser.NdefMessageParser;
import com.test.monopolyextensionNFC.record.ParsedNdefRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements CagaJugadores.OnFragmentInteractionListener,CagaJugadores.InterfaceCommunicator {

    private static final int MY_REQUEST_CODE = 23;
    //    private FirebaseAuth mFirebaseAuth;
//    private FirebaseUser mFirebaseUser;
//    private FirebaseFirestore db;
    private String mUsername, name, mPhotoUrl;
    private Map<String, Object> maping;
    private ArrayList<Jugadores> juga;
    private Jugadores juga1,juga2;
    private ListView listaJug;
    private LeadsAdapter mLeadsAdapter;
    private Resources reso;
    private EditText cobraTxt, pagaTxt;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private TextView text1,text2,ultiMov;
    private CheckBox checkBank;
    private FragmentManager fm;
    private CagaJugadores alertDialog;
    private boolean darkMode=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (AppCompatDelegate.getDefaultNightMode()
                ==AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.Platform_MaterialComponents);
            darkMode = true;
        }
        if (AppCompatDelegate.getDefaultNightMode()
                ==AppCompatDelegate.MODE_NIGHT_NO) {
            setTheme(R.style.ThemeOverlay_MaterialComponents);
            darkMode = false;
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listaJug = findViewById(R.id.listaJuga);
        cobraTxt = findViewById(R.id.cobraTxt);
        pagaTxt = findViewById(R.id.pagaTxt);
//        spinJuga = findViewById(R.id.spinPaga);
        text1 = findViewById(R.id.juga1Txt);
        text2 = findViewById(R.id.juga2Txt);
        ultiMov = findViewById(R.id.ultiMov);
        checkBank = findViewById(R.id.checkBank);
        listaJug.setFocusable(false);


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, R.string.NoNFC, Toast.LENGTH_SHORT).show();
            text1.setText(R.string.NoNFC);
            text2.setText(R.string.NoNFC);
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        maping = new HashMap<>();
        juga = new ArrayList<>();
        //inicializa jugadores leyendo codigo rfid


        //cargar la list view
        // Instancia del ListView.
        listaJug = (ListView) findViewById(R.id.listaJuga);

        // Inicializar el adaptador con la fuente de datos.
        mLeadsAdapter = new LeadsAdapter(this,
                LeadsRepository.getInstance().getLeads());

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "jugadores", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor fila = db.rawQuery("select id ,nombre ,dinero,perfil from jugadores", null);
        if(fila.moveToFirst()) {
            do {
                long id =fila.getLong(0);
                String nom= fila.getString(1);
                long din = fila.getLong(2);
                int ima=fila.getInt(3);
                mLeadsAdapter.add(new Jugadores(id,nom,din,ima));
            } while (fila.moveToNext());
        }
        db.close();

        //TO DO : Implementar historial de movimientos

        //Relacionando la lista con el adaptador
        listaJug.setAdapter(mLeadsAdapter);
        /*
Eventos
listaJug.setOnItemClickListener(new AdapterView.OnItemClickListener() {
@Override
public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
Jugadores currentLead = mLeadsAdapter.getItem(position);
Toast.makeText(getApplicationContext(),
"Iniciar screen de detalle para: \n" + currentLead.getNombreJuga(),
Toast.LENGTH_LONG).show();
juga1 = currentLead;
}
});
ArrayList<String> elementosLista = new ArrayList<String>();
for (int i = 0; i < listaJug.getCount(); i++) {
elementosLista.add(mLeadsAdapter.getItem(i).getNombreJuga());
}
adapterList = new ArrayAdapter<>(
this,
R.layout.row_names,
elementosLista
);
spinJuga.setAdapter(adapterList);
*/

    }


    public void onPause() {

        super.onPause();
    }

    public void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled())
                showWirelessSettings();

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    public void showAlertDialog(View v) {
        fm = getSupportFragmentManager();
        alertDialog = CagaJugadores.newInstance("", "");

        alertDialog.show(fm, "fragment_alert");
    }

    public void cobrar(View v) {

        if (juga1 == null ) {
            Toast.makeText(this, "Debe seleccionar un jugador", Toast.LENGTH_LONG).show();
        } else {
            cobra(juga1, Long.parseLong(cobraTxt.getText().toString()));
            mLeadsAdapter.notifyDataSetChanged();
            System.out.println(juga1.getNombreJuga() + " cobro " + " $" + cobraTxt.getText().toString());
//        spinJuga.setSelection(-1);
            limpiarSel();
        }
    }

    public void pagar(View v) {
        if (  !checkBank.isChecked() && (juga1==null || juga2 == null)) {
            Toast.makeText(this, "Debe seleccionar ambos jugadores", Toast.LENGTH_LONG).show();
        } else {
            if(checkBank.isChecked()){
                pagaBanco(juga1,Long.parseLong(pagaTxt.getText().toString()));
            }else {
                paga(juga1, juga2, Long.parseLong(pagaTxt.getText().toString()));
                System.out.println(juga1.getNombreJuga() + " le pago a " + juga2.getNombreJuga() + " $" + pagaTxt.getText().toString());
            }
            mLeadsAdapter.notifyDataSetChanged();
            limpiarSel();
        }
    }

    public void pagaBanco (Jugadores juga1,long monto){
        juga1.resta(monto);
        ultiMov.setText("El Jugador "+juga1.getNombreJuga()+" le pago al Banco $"+monto);
    }
    public void paga(Jugadores juga1, Jugadores juga2, long monto) {

        juga1.resta(monto);
        juga2.suma(monto);
        ultiMov.setText("El Jugador "+juga1.getNombreJuga()+" le pago a "+juga2.getNombreJuga()+" $"+monto);
    }

    public void cobra(Jugadores juga, long monto) {
        juga.suma(monto);
        ultiMov.setText("El Jugador "+juga.getNombreJuga()+" cobro $"+monto);
    }

    public void limpiarSeleccion(View v){
        limpiarSel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if(darkMode){
            menu.findItem(R.id.darkMode).setTitle("Desactivar modo oscuro");
        }else{
            menu.findItem(R.id.darkMode).setTitle("Activar modo oscuro");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.start) {
//            this.adapterList.clear();
            for (int i = 0; i < mLeadsAdapter.getCount(); i++) {
                borraJuga(mLeadsAdapter.getItem(i).getId());
            }
            this.mLeadsAdapter.clear();

        }
        if(item.getItemId()==R.id.darkMode){
            if (!darkMode) {
                AppCompatDelegate
                        .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else {
                AppCompatDelegate
                        .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            recreate();
        }
        if(item.getItemId()==R.id.borrarJuga){
            if (juga1 == null) {
                Toast.makeText(this, "Debe seleccionar un jugador", Toast.LENGTH_LONG).show();
            } else {
                mLeadsAdapter.remove(juga1);
                borraJuga(juga1.getId());
                limpiarSel();
            }
        }
        return true;
    }
    public void limpiarSel(){

        pagaTxt.setText("");
        cobraTxt.setText("");
        juga1 = null;
        juga2 = null;
        checkBank.setChecked(false);
        checkBank.setClickable(true);
        text1.setText("");
        text2.setText("");
    }


    @Override
    public void sendRequestCode(int code, Jugadores juga) {
        if (code == 1) {
            for(int i=0;i<mLeadsAdapter.getCount();i++){
                if(juga.getId()==mLeadsAdapter.getItem(i).getId()){
                    AlertDialog.Builder bui =new AlertDialog.Builder(this);
                    bui.setMessage("El jugador ya existe, prueba con otra tarjeta u otro id, si crees que es un error" +
                            "prueba a reiniciar la app").
                            setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            }).setNegativeButton("Reiniciar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            recreate();
                        }
                    }).show();
//                    Toast.makeText(this,"2do return",Toast.LENGTH_LONG).show();
                    return;
                }
            }

            System.out.println("Roger that");
            this.mLeadsAdapter.add(juga);
            alta(juga);
            this.mLeadsAdapter.notifyDataSetChanged();
            Toast.makeText(this,"Creado",Toast.LENGTH_LONG).show();
//            this.adapterList.add(juga.getNombreJuga());
//            this.adapterList.notifyDataSetChanged();
        }
    }


    private String dumpTagData(Tag tag) {
        byte[] id = tag.getId();
        String idd= ""+toDec(id);
        return idd;
    }
    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Check if the fragment is an instance of the right fragment
        if (alertDialog instanceof CagaJugadores) {
            CagaJugadores my = (CagaJugadores) alertDialog;
            // Pass intent or its data to the fragment's method
            my.resolveIntent(intent);
        }
            setIntent(intent);
            resolveIntent(intent);


    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;

            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];

                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

            } else {
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }

            displayMsgs(msgs);
        }
    }


    private void displayMsgs(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0)
            return;

        StringBuilder builder = new StringBuilder();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
        final int size = records.size();

        for (int i = 0; i < size; i++) {
            ParsedNdefRecord record = records.get(i);
            String str = record.str();
            builder.append(str);
        }

        marcaString(builder.toString());
    }
    private void marcaString(String str){
        System.out.println(str);
        System.out.println(""+mLeadsAdapter.getCount());
        for(int i=0;i<mLeadsAdapter.getCount();i++){
            System.out.println(""+mLeadsAdapter.getItem(i).getId());
            if(str.equals(""+mLeadsAdapter.getItem(i).getId())) {
                listaJug.setSelection(i);
                Jugadores currentLead = mLeadsAdapter.getItem(i);

                if (juga1 == null) {
                    juga1 = currentLead;
                    text1.setText(juga1.getNombreJuga());
                } else{
                    juga2 = currentLead;
                    text2.setText(juga2.getNombreJuga());
                    checkBank.setChecked(false);
                    checkBank.setClickable(false);
                    if(juga2==juga1){
                        checkBank.setChecked(true);
                        text2.setText("Banco");
                    }
                }

                return;
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void alta(Jugadores ju) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "jugadores", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String id =""+ ju.getId();
        String nom = ju.getNombreJuga();
        String din ="" +ju.getDinero();
        String ima =""+ju.getPerfil();
        ContentValues registro = new ContentValues();
        registro.put("id", id);
        registro.put("nombre", nom);
        registro.put("dinero", din);
        registro.put("perfil", ima);
        bd.insert("jugadores", null, registro);
        bd.close();
//        Toast.makeText(this, "Se cargaron los datos del artículo",
//                Toast.LENGTH_SHORT).show();
    }

    public void borraJuga(long id) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "jugadores", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        int cant = bd.delete("jugadores", "id=" + id, null);
        bd.close();
        if (cant == 1)
            Toast.makeText(this, "Se borró el jugador",
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "No se pudo borrar el jugador",
                    Toast.LENGTH_SHORT).show();
    }

    public void modificacion(Jugadores viejo,Jugadores nuevo) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "jugadores", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        String id =""+ nuevo.getId();
        String nom = nuevo.getNombreJuga();
        String din ="" +nuevo.getDinero();
        String ima =""+nuevo.getPerfil();
        ContentValues registro = new ContentValues();
        registro.put("id", id);
        registro.put("nombre", nom);
        registro.put("dinero", din);
        registro.put("perfil", ima);
        int cant = bd.update("jugadores", registro, "codigo=" + viejo.getId(), null);
        bd.close();
        if (cant == 1)
            Toast.makeText(this, "se modificaron los datos", Toast.LENGTH_SHORT)
                    .show();
        else
            Toast.makeText(this, "no existe el jugador",
                    Toast.LENGTH_SHORT).show();
    }
}