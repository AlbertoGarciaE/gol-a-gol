package com.google.android.gcm.GolAGol.model;

/**
 * This class represents a match and hold all the information necessari
 */
public class Match {
    private String matchId;
    private String local;
    private String away;
    private int localScore;
    private int awayScore;
    private String status;
    private String log = "";

    public static String STATUS_ONGOING = "ONGOING";
    public static String STATUS_HALFTIME = "HALFTIME";
    public static String STATUS_FINISHED = "FINISHED";
    public static String STATUS_TOSTART = "TOSTART";

    public Match(String matchId) {
        this.matchId = matchId;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getAway() {
        return away;
    }

    public void setAway(String away) {
        this.away = away;
    }

    public int getLocalScore() {
        return localScore;
    }

    public void setLocalScore(int localScore) {
        this.localScore = localScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void appendLine(String line) {
        log = log + "\n" + line;
    }

    public String getLog() {
        return log;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return matchId.equals(match.matchId);

    }

    @Override
    public int hashCode() {
        return matchId.hashCode();
    }

    @Override
    public String toString() {
        return "Match{" +
                "matchId='" + matchId + '\'' +
                ", local='" + local + '\'' +
                ", away='" + away + '\'' +
                ", localScore=" + localScore +
                ", awayScore=" + awayScore +
                ", status='" + status + '\'' +
                '}';
    }
}
