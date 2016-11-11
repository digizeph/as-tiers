import bgpi.utils.io.FileOp;
import bgpi.utils.io.Output;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Mingwei Zhang on 11/10/16.
 */
public class AsTiers {

    List<Set<String>> learnTiers(String folder, String filename) {
        List<Set<String>> tiers = new ArrayList<>();
        Map<String, ArrayList<Integer>> peerProviderCount = new HashMap<>();
        BufferedReader bi = FileOp.getBufferedReader(folder, filename);
        String line;
        try {
            while ((line = bi.readLine()) != null) {
                if (line.contains("#")) {
                    continue;
                }
                String[] lst = line.split("\\|");
                if (!peerProviderCount.containsKey(lst[0])) {
                    ArrayList<Integer> list = new ArrayList<>();
                    list.add(0);
                    list.add(0);
                    peerProviderCount.put(lst[0], list);
                }

                if (!peerProviderCount.containsKey(lst[1])) {
                    ArrayList<Integer> list = new ArrayList<>();
                    list.add(0);
                    list.add(0);
                    peerProviderCount.put(lst[1], list);
                }

                List<Integer> list;
                if (Objects.equals(lst[2], "0")) {
                    // peers
                    list = peerProviderCount.get(lst[0]);
                    list.set(0, list.get(0) + 1);
                    list = peerProviderCount.get(lst[1]);
                    list.set(0, list.get(0) + 1);
                } else {
                    list = peerProviderCount.get(lst[1]);
                    list.set(1, list.get(1) + 1);
                }
            }
            bi.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tiers.add(new HashSet<String>());
        tiers.add(new HashSet<String>());
        tiers.add(new HashSet<String>());

        for (String asn : peerProviderCount.keySet()) {
            ArrayList<Integer> pair = peerProviderCount.get(asn);
            if (pair.get(0) > 0 && pair.get(1) == 0) {
                // no providers, only peers --> tier 1
                tiers.get(0).add(asn);
            } else if (pair.get(0) == 0 && pair.get(1) > 0) {
                // only providers, no peers --> tier 3
                tiers.get(2).add(asn);
            } else {
                // hybrid of both
                tiers.get(1).add(asn);
            }
        }

        Output.pl("tier1: %d; tier2: %d; tier3: %d", tiers.get(0).size(), tiers.get(1).size(), tiers.get(2).size());
        return tiers;
    }
}
