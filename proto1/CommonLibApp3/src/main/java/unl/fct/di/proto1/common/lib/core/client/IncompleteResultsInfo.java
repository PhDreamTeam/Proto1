package unl.fct.di.proto1.common.lib.core.client;

/**
 * Created by AT DR on 09-04-2015.
 *
 */
public class IncompleteResultsInfo {
    boolean hasIncompleteResults = false;
    String failureReason = null;

    public void setIncompleteResultsInfo(String failureReason) {
        this.hasIncompleteResults = true;
        this.failureReason = failureReason;
    }

    public boolean hasIncompleteResults() {
        return hasIncompleteResults;
    }

    public String getFailureReason() {
        return failureReason;
    }

    @Override
    public String toString() {
        return hasIncompleteResults ? "incomplete results: " + getFailureReason() : "complete results";

    }
}
