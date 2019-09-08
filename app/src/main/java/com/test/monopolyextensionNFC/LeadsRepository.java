package com.test.monopolyextensionNFC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LeadsRepository {

    private static LeadsRepository repository = new LeadsRepository();
    private HashMap<Long, Jugadores> leads = new HashMap<>();

    public static LeadsRepository getInstance() {
        return repository;
    }

    private LeadsRepository() {
//        saveLead(new Jugadores(1,"Alexander Pierrot", 15000, R.drawable.auto));
//        saveLead(new Jugadores(2,"Carlos Lopez", 15000, R.drawable.barco));
//        saveLead(new Jugadores(3,"Sara Bonz", 15000, R.drawable.carretilla));
//        saveLead(new Jugadores(4,"Liliana Clarence", 15000, R.drawable.perro));
    }

    private void saveLead(Jugadores lead) {
        leads.put(lead.getId(), lead);
    }

    public List<Jugadores> getLeads() {
        return new ArrayList<>(leads.values());
    }
}
