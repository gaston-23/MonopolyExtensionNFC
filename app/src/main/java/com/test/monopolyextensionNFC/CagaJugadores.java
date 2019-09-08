package com.test.monopolyextensionNFC;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.test.monopolyextensionNFC.parser.NdefMessageParser;
import com.test.monopolyextensionNFC.record.ParsedNdefRecord;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CagaJugadores.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CagaJugadores#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CagaJugadores extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EditText nomTxt,idTxt,dineroTxt;
    private Spinner mySpinner;
    private Button saveBtn;
    public InterfaceCommunicator interfaceCommunicator;
    public Jugadores nuevo;
    public String data;

//    public static interface OnCompleteListener {
//        public abstract void onComplete(String time);
//    }

//    private OnCompleteListener mListener2;

    // make sure the Activity implemented it
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            this.mListener = (OnCompleteListener)activity;
//        }
//        catch (final ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
//        }
//    }

    public interface InterfaceCommunicator {
        void sendRequestCode(int code,Jugadores juga);
    }
    public CagaJugadores() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CagaJugadores.
     */
    // TODO: Rename and change types and number of parameters
    public static CagaJugadores newInstance(String param1, String param2) {
        CagaJugadores fragment = new CagaJugadores();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

private static Integer[] imageIconDatabase = { R.drawable.auto,
        R.drawable.perro, R.drawable.carretilla, R.drawable.barco,
        R.drawable.plancha,R.drawable.sombrero};

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nomTxt = view.findViewById(R.id.nombre);
        idTxt = view.findViewById(R.id.idTarjeta);
        mySpinner = view.findViewById(R.id.spinImg);
        saveBtn = view.findViewById(R.id.saveBtn);
        dineroTxt = view.findViewById(R.id.dineroTxt);

        getDialog().setTitle("Agrega Jugadores");
        nomTxt.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        SimpleImageArrayAdapter adapter = new SimpleImageArrayAdapter(view.getContext(),
                imageIconDatabase);

        mySpinner.setAdapter(adapter);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarJuga(view);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_caga_jugadores, container, true);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        interfaceCommunicator = (InterfaceCommunicator) context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    public void guardarJuga(View v){
        long dinero=0;
        if(!(idTxt.getText()==null || nomTxt.getText()==null || dineroTxt.getText()==null)){
            dinero= Long.parseLong(dineroTxt.getText().toString().replace("$","0"));
            int img= imageIconDatabase[mySpinner.getSelectedItemPosition()];
            nuevo = new Jugadores(Long.parseLong(idTxt.getText().toString()),nomTxt.getText().toString(),dinero,img);

//            getTargetFragment().onActivityResult(getTargetRequestCode(), 1, getActivity().getIntent());

            interfaceCommunicator.sendRequestCode(1,nuevo); // the parameter is any int code you choose.

            System.out.println("se carga?");
            dismiss();

        }else{
            Toast.makeText(v.getContext(),"Complete todos los campos",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

    @Override
    public void startIntentSenderForResult(IntentSender intent, int requestCode, @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        super.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }

    public void resolveIntent(Intent intent) {
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

        idTxt.setText(builder.toString());
    }
}
