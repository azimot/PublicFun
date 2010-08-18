import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.joda.time.DateTime;

import net.sf.stackwrap4j.entities.Reputation;
import net.sf.stackwrap4j.entities.User;
import net.sf.stackwrap4j.exceptions.ParameterNotSetException;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.query.ReputationQuery;

/**
 * I don't want this class to know anything about color graphics, poitns, or
 * anything like that.
 * 
 * @author u0117691
 * 
 */
public class ReputationGraph extends JPanel {

    private GraphPanel graph;
    private InfoPane info;

    private StackWrapDataAccess data;
    private Map<Integer, User> users;
    private Map<Integer, List<Reputation>> userRep;

    // private Map<Integer, List<RepPoint>> dailyPoints;

    public ReputationGraph() throws IOException, JSONException {
        super(new BorderLayout());
        users = new HashMap<Integer, User>();
        userRep = new HashMap<Integer, List<Reputation>>();
        // dailyPoints = new HashMap<Integer, List<RepPoint>>();
        data = new StackWrapDataAccess("RhtZB9-r0EKYJi-OjKSRUg");
        graph = new GraphPanel(this);
        add(graph);
    }

    public void addUser(String site, int userId) throws JSONException,
            IOException, ParameterNotSetException {
        AddUserWorker worker = new AddUserWorker(userId, site);
        worker.execute();
    }

    private List<RepPoint> calculateDailyPoints(int userId) {
        List<RepPoint> points = new ArrayList<RepPoint>();
        int totalRep = 0;
        RepPoint currentDay = null;
        for (Reputation r : userRep.get(userId)) {
            currentDay = setCurrentDay(currentDay, r, points);
            currentDay.addRep(r);
            totalRep -= r.getNegativeRep();
            totalRep += r.getPositiveRep();
        }
        return points;
    }

    private RepPoint setCurrentDay(RepPoint currentDay,
            Reputation currentPoint, List<RepPoint> points) {
        if (currentDay == null) {
            RepPoint newDay = new RepPoint(currentPoint.getOnDate() * 1000);
            points.add(newDay);
            return newDay;
        } else {
            DateTime dateInList = currentDay.getDate();
            DateTime dateLookingAt = new DateTime(
                    currentPoint.getOnDate() * 1000);
            if ((dateLookingAt.getMillis() - dateInList.getMillis()) > (60 * 60 * 24)) {
                RepPoint newDay = new RepPoint(currentPoint.getOnDate() * 1000);
                points.add(newDay);
                return newDay;
            } else {
                return currentDay;
            }
        }
    }

    private class AddUserWorker extends SwingWorker<List<RepPoint>, Void> {

        private int userId;
        private String site;

        public AddUserWorker(int userId, String site) {
            this.userId = userId;
            this.site = site;
        }

        @Override
        protected List<RepPoint> doInBackground() throws Exception {
            User newU = data.getUser(site, userId);
            users.put(userId, newU);
            ReputationQuery query = new ReputationQuery();
            query.setPageSize(ReputationQuery.MAX_PAGE_SIZE);
            List<Reputation> userRepL = newU.getReputationInfo(query);
            Collections.sort(userRepL, new Comparator<Reputation>() {
                @Override
                public int compare(Reputation o1, Reputation o2) {
                    return (int) (o1.getOnDate() - o2.getOnDate());
                }
            });
            userRep.put(userId, userRepL);
            List<RepPoint> reps = calculateDailyPoints(userId);
            graph.addUser(users.get(userId), reps);
            return reps;
        }

        @Override
        protected void done() {
            super.done();
            ReputationGraph.this.repaint();
        }
    }
}
