package com.google.android.gcm.demo.logic;

import com.google.android.gcm.demo.model.Match;

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

    public boolean removeMatch(Match match) {
        return this.matchList.remove(match);
    }

    public Match getMatch(String id) {
        Match aux = new Match(id);
        return this.matchList.get(this.matchList.indexOf(aux));
    }
}
