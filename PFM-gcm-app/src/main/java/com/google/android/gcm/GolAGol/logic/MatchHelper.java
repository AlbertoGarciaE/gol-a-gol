package com.google.android.gcm.GolAGol.logic;

import com.google.android.gcm.GolAGol.model.Match;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Usuario on 09/08/2015.
 */
public class MatchHelper {

    private List<Match> matchList;
    private static MatchHelper instance;


    private MatchHelper() {
        this.matchList = new ArrayList<>();
    }

    public static MatchHelper getInstance() {
        if (instance == null) {
            instance = new MatchHelper();
        }
        return instance;
    }

    public List<Match> getMatchList() {
        return matchList;
    }

    public boolean addMatch(Match match) {
        return this.matchList.add(match);
    }

    public boolean removeMatch(String id) {
        Match aux = getMatch(id);
        boolean result = false;
        if (aux != null) {
            result = this.matchList.remove(aux);
        }
        return result;
    }

    public void clearMatchList() {
        this.matchList.clear();
    }

    public Match getMatch(String id) {
        Match aux = new Match(id);
        int index = this.matchList.indexOf(aux);
        if (index >= 0) {
            aux = this.matchList.get(index);
        } else {
            aux = null;
        }
        return aux;
    }

    public void initMatchList(List<Match> list) {
        this.matchList = new ArrayList<>(list);
    }
}
