import java.io.*;
import java.util.*;

/**
 * Created by Mingwei Zhang on 11/10/16.
 */
public class AsTiers {

    List<Set<String>> learnTiers(String filename) {
        List<Set<String>> tiers = new ArrayList<>();
        Map<String, ArrayList<Integer>> peerProviderCount = new HashMap<>();
        BufferedReader bi = null;

        try {
            FileReader fr = new FileReader(filename);
            bi = new BufferedReader(fr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        String line;
        try {
            while ((line = bi.readLine()) != null) {
                if (line.contains("#")) {
                    continue;
                }
                String[] lst;
                if(line.contains("\\|"))
                    lst = line.split("\\|");
                else
                    lst = line.split(" ");
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

        System.out.printf("tier1: %d; tier2: %d; tier3: %d\n", tiers.get(0).size(), tiers.get(1).size(), tiers.get(2).size());
        return tiers;
    }


    void writeTiers(String filename, List<Set<String>> tiers){

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

    public static void main(String[] args) {
        if(args.length!=2){
            System.err.println("USAGE: java -jar astiers INPUTFILE OUTPUTFILE");
            return;
        }
        AsTiers astiers = new AsTiers();
        List<Set<String>> tiers = astiers.learnTiers(args[0]);
        astiers.writeTiers(args[1],tiers);
    }
}
