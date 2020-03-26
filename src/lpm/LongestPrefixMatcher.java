/**
 * LongestPrefixMatcher.java
 * <p>
 * Version: 2019-07-10
 * Copyright: University of Twente, 2015-2019
 * <p>
 * *************************************************************************
 * Copyright notice                            *
 * *
 * This file may ONLY be distributed UNMODIFIED.              *
 * In particular, a correct solution to the challenge must NOT be posted  *
 * in public places, to preserve the learning effect for future students. *
 * *************************************************************************
 */

package lpm;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LongestPrefixMatcher {
    List<String> routes = new ArrayList<>();
    BufferedReader reader;

    /**
     * You can use this function to initialize variables.
     */
    public LongestPrefixMatcher() {
        try {
            reader = new BufferedReader(new FileReader("routes.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Looks up an IP address in the routing tables
     *
     * @param ip The IP address to be looked up in integer representation
     * @return The port number this IP maps to
     */
    public int lookup(int ip) throws IOException {
        int first = Integer.parseInt(ipToHuman(ip).split("\\.")[0]);
        String temp = "";
        String result = "-1";
        int index = 0;
        if (ip >= 0 && ip < Integer.parseInt(routes.get(0).split(":")[0])
                || first > 223) {
            return -1;
        }
        //Start looking for the first negative IP
        int firstneg = routes.size() * 2 / 5;
        for (int i = routes.size() / 3; i < routes.size(); i += 100) {
            temp = routes.get(i);
            if (Integer.parseInt(temp.split(":")[0]) < 0) {
                firstneg = i;
                break;
            }
        }
        for (int i = 0; i < 100; i++) {
            temp = routes.get(firstneg - i);
            if (Integer.parseInt(temp.split(":")[0]) >= 0) {
                firstneg += -i + 1;
                break;
            }
        }
        //Binary search
        int oldIndex = 0;
        int middle = 0;
        if (ip > 0) {
            while (true) {
                oldIndex = index;
                index = (firstneg + index)/2;
                middle = Integer.parseInt(routes.get(index).split(":")[0]);
                if (middle > ip) {
                    firstneg = index;
                    index = oldIndex;
                } else if (Integer.parseInt(ipToHuman(middle).split("\\.")[0]) < first) {
                    //loop again with index now bigger
                } else {
                    break;
                }
            }
        } else {
            index = firstneg;
            firstneg = routes.size();
            while (true) {
                oldIndex = index;
                index = (firstneg + index) / 2;
                middle = Integer.parseInt(routes.get(index).split(":")[0]);
                if (middle > ip) {
                    firstneg = index;
                    index = oldIndex;
                } else if (Integer.parseInt(ipToHuman(middle).split("\\.")[0]) < first) {
                    //loop again with index now bigger
                } else {
                    break;
                }
            }
        }
        //Loop through the list +100 until firsttemp > first
        int firsttemp = -1;
        for (int i = index; true; i += 100) {
            if (i >= routes.size() - 1) {
                index = routes.size() - 1;
                break;
            }
            firsttemp = Integer.parseInt(ipToHuman(Integer.parseInt(routes.get(i).split(":")[0])).split("\\.")[0]);
            if (Integer.parseInt(routes.get(i).split(":")[0]) > ip || firsttemp > first) {
                index = i - 1;
                break;
            }
        }
        //Go back to last ip with same first
        for (int i = 0; index - i > 0; i++) {
            firsttemp = Integer.parseInt(ipToHuman(Integer.parseInt(routes.get(index - i).split(":")[0])).split("\\.")[0]);
            if (firsttemp <= first) {
                index -= i;
                temp = routes.get(index);
                break;
            }
        }
        result = routes.get(index);
        String[] split = result.split(":");
        String endResult = "-1";
        int firstIndex = index;
        //Make input ip into 32 bit binary
        String[] aypee = ipToHuman(ip).split("\\.");
        String ipee = "";
        for (String a : aypee) {
            String tempo = String.format("%8s", Integer.toBinaryString(Integer.parseInt(a))).replace(' ', '0');
            ipee += tempo;
        }
        //Find right port
        int previousFirst = Integer.parseInt(ipToHuman(Integer.parseInt(result.split(":")[0])).split("\\.")[0]);
        while (true) {
            first = Integer.parseInt(ipToHuman(Integer.parseInt(result.split(":")[0])).split("\\.")[0]);
            temp = "";
            aypee = ipToHuman(Integer.parseInt(split[0])).split("\\.");
            //make current try 32 bit binary
            for (String a : aypee) {
                String tempo = String.format("%8s", Integer.toBinaryString(Integer.parseInt(a))).replace(' ', '0');
                temp += tempo;
            }
            //cut IPs down to prefix bits
            temp = temp.substring(0, Integer.parseInt(split[1]));
            String ipeesub = ipee.substring(0, Integer.parseInt(split[1]));
            //if true, break
            if (temp.equals(ipeesub)) {
                endResult = split[2];
                break;
            }
            index--;
            if (index < 0 || first != previousFirst) {
                break;
            }
            result = routes.get(index);
            split = result.split(":");
            previousFirst = first;
        }
        return Integer.parseInt(endResult);
    }

    /**
     * Adds a route to the routing tables
     *
     * @param ip           The IP the block starts at in integer representation
     * @param prefixLength The number of bits indicating the network part
     *                     of the address range (notation ip/prefixLength)
     * @param portNumber   The port number the IP block should route to
     */
    public void addRoute(int ip, byte prefixLength, int portNumber) {
        routes.add(ip + ":" + prefixLength + ":" + portNumber);
    }

    /**
     * This method is called after all routes have been added.
     * You don't have to use this method but can use it to sort or otherwise
     * organize the routing information, if your datastructure requires this.
     */
    public void finalizeRoutes() {
        // TODO: Optionally do something     
    }

    /**
     * Converts an integer representation IP to the human readable form
     *
     * @param ip The IP address to convert
     * @return The String representation for the IP (as xxx.xxx.xxx.xxx)
     */
    private String ipToHuman(int ip) {
        return Integer.toString(ip >> 24 & 0xff) + "." +
                Integer.toString(ip >> 16 & 0xff) + "." +
                Integer.toString(ip >> 8 & 0xff) + "." +
                Integer.toString(ip & 0xff);
    }

    /**
     * Parses an IP
     *
     * @param ipString The IP address to convert
     * @return The integer representation for the IP
     */
    private int parseIP(String ipString) {
        String[] ipParts = ipString.split("\\.");

        int ip = 0;
        for (int i = 0; i < 4; i++) {
            ip |= Integer.parseInt(ipParts[i]) << (24 - (8 * i));
        }

        return ip;
    }
}
