import java.io.*;
import java.util.*;

/**
 * AsTiers: exporting tier information.
 *
 * Created by Mingwei Zhang on 11/10/16.
 *
 */
public class AsTiers {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("USAGE: java -jar astiers INPUTFILE OUTPUTFILE");
            return;
        }
        AsTiers astiers = new AsTiers();
        List<Set<String>> tiers = astiers.learnTiers(args[0]);
        astiers.writeTiers(args[1], tiers);
    }


    /**
     * Learn tiers using CAIDA AS relationship data.
     *
     * @param filename path file name.
     * @return list of sets with 3 items: tier 1 ASes, tier 2 ASes, tier 3 ASes.
     */
    private List<Set<String>> learnTiers(String filename) {
        List<Set<String>> tiers = new ArrayList<>();    // tiers
        Map<String, ArrayList<Integer>> peerCustomerProviderCount = new HashMap<>();    // number of peers, and number of providers
        BufferedReader bi;

        // Open input file.
        try {
            FileReader fr = new FileReader(filename);
            bi = new BufferedReader(fr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        // Read through CAIDA relationship file.
        String line;
        try {
            while ((line = bi.readLine()) != null) {
                if (line.contains("#")) {
                    continue;
                }
                String[] lst;
                if (line.contains("|"))
                    lst = line.split("\\|");
                else
                    lst = line.split(" ");


                // create entry if first AS is not in the map.
                if (!peerCustomerProviderCount.containsKey(lst[0])) {
                    ArrayList<Integer> list = new ArrayList<>();
                    list.add(0);
                    list.add(0);
                    list.add(0);
                    peerCustomerProviderCount.put(lst[0], list);
                }

                // create entry if second AS is not in the map.
                if (!peerCustomerProviderCount.containsKey(lst[1])) {
                    ArrayList<Integer> list = new ArrayList<>();
                    list.add(0);
                    list.add(0);
                    list.add(0);
                    peerCustomerProviderCount.put(lst[1], list);
                }

                // relationship: 0 - peer, 1 - AS1 is AS2's provider.
                List<Integer> list;
                if (Objects.equals(lst[2], "0")) {
                    // peers
                    list = peerCustomerProviderCount.get(lst[0]);
                    list.set(0, list.get(0) + 1);
                    list = peerCustomerProviderCount.get(lst[1]);
                    list.set(0, list.get(0) + 1);
                } else {
                    // add AS1's number of customers by 1
                    list = peerCustomerProviderCount.get(lst[0]);
                    list.set(1, list.get(1) + 1);
                    // add AS2's number of providers by 1
                    list = peerCustomerProviderCount.get(lst[1]);
                    list.set(2, list.get(2) + 1);
                }
            }
            bi.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create sets for each tier.
        tiers.add(new HashSet<String>());
        tiers.add(new HashSet<String>());
        tiers.add(new HashSet<String>());

        for (String asn : peerCustomerProviderCount.keySet()) {
            ArrayList<Integer> pair = peerCustomerProviderCount.get(asn);
            if (pair.get(1) > 0 && pair.get(2) == 0) {
                // has customer, no providers
                tiers.get(0).add(asn);
            } else if (pair.get(1) == 0 && pair.get(2) > 0) {
                // only providers, no customers --> tier 3
                tiers.get(2).add(asn);
            } else {
                // hybrid of both
                tiers.get(1).add(asn);
            }
        }

        System.out.printf("tier1: %d; tier2: %d; tier3: %d\n", tiers.get(0).size(), tiers.get(1).size(), tiers.get(2).size());
        return tiers;
    }

    /**
     * Write out tier information to file.
     * Format: TIER:ASN
     *
     * @param filename output file name
     * @param tiers    list of ASes for each tier.
     */
    private void writeTiers(String filename, List<Set<String>> tiers) {

        if (tiers==null){
            return;
        }

        try {
            // create output file if not exists.
            File f = new File(filename);
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);

            for(int i=0;i<tiers.size();i++){
                for(String asn: tiers.get(i)){
                        bw.write(String.format("%d:%s\n",i+1,asn));
                }
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
