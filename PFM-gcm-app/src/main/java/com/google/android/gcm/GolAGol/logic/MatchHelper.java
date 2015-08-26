package com.google.android.gcm.GolAGol.logic;

import com.google.android.gcm.GolAGol.model.Match;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds a mach list as a singleton, and
 * implements the most common actions to manage the list of matches
 */
public class MatchHelper {

    private List<Match> matchList;
    private static MatchHelper instance;


    private MatchHelper() {
        this.matchList = new ArrayList<>();
    }

    /**
     * Get an instance of the class in order to access the list
     *
     * @return MatchHelper
     */
    public static MatchHelper getInstance() {
        if (instance == null) {
            instance = new MatchHelper();
        }
        return instance;
    }

    /**
     * Get the full list of matches
     *
     * @return List<Match>
     */
    public List<Match> getMatchList() {
        return matchList;
    }

    /**
     * Add a match to the list
     *
     * @param match
     * @return True if is correct, false otherwise
     */
    public boolean addMatch(Match match) {
        return this.matchList.add(match);
    }

    /**
     * Remove the match with the given id
     *
     * @param id Unique id of the match
     * @return True if is correct, false otherwise
     */
    public boolean removeMatch(String id) {
        Match aux = getMatch(id);
        boolean result = false;
        if (aux != null) {
            result = this.matchList.remove(aux);
        }
        return result;
    }

    /**
     * Erase the full list of matches
     */
    public void clearMatchList() {
        this.matchList.clear();
    }

    /**
     * Get the match with the given id
     *
     * @param id Unique id of the match
     * @return Match
     */
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

    /**
     * Override the current match list with the given list
     *
     * @param list The new match list
     */
    public void initMatchList(List<Match> list) {
        this.matchList = new ArrayList<>(list);
    }
}
