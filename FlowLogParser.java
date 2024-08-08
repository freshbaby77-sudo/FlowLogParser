// Online Java Compiler
// Use this editor to write, compile and run your Java code online
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

class FlowLogParser {
    private static final Logger logger = Logger.getLogger(FlowLogParser.class.getName());

    // usage
	// 1st arg: flow record log plain text file
	// 2nd arg: tag look up table plain text file
    public static void main(String[] args) {
		logger.info("Starting FlowLogParser");
		if(args.length < 2){
			logger.warning("Please provide the flow log and tag look up files");
			return;
		}
        parse(args[0], buildTagMap(args[1]));
    }
    
    public static void parse(String file, Map<String, Map<String, String>> tagMap) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter tag_count_writer = new BufferedWriter(new FileWriter(
                    "tagCount.txt"));
            BufferedWriter combination_count_writer = new BufferedWriter(new FileWriter(
                    "combinationCount.txt"));
            Map<String, Integer> tagCountMap = new HashMap<>();
            Map<String, Map<String, Integer>> combinationCountMap = new HashMap<>();

            String str;
            while ((str=br.readLine()) != null) {
                // do something with each line
                String[] log = str.split("[ ,\t]+"); // assume the log field is delimieted by comma
                String dstport = log[6];
                String protocol = Constants.PROTOCOL_MAP.get(Integer.parseInt(log[7]));
                
                String tag = "unTagged";
                if(tagMap.containsKey(protocol)){
                    if(tagMap.get(protocol).containsKey(dstport)){
                        tag = tagMap.get(protocol).get(dstport);
                    }
                }
                
                tagCountMap.put(tag, tagCountMap.getOrDefault(tag, 0)+1);
                if(!combinationCountMap.containsKey(protocol)){
                    combinationCountMap.put(protocol, new HashMap<>());
                }
                combinationCountMap.get(protocol).put(dstport, combinationCountMap.get(protocol).getOrDefault(dstport, 0)+1);
            }
            
            for(Map.Entry<String, Integer> entry : tagCountMap.entrySet()){
                tag_count_writer.write(entry.getKey()+","+entry.getValue() + "\n");
            }
            tag_count_writer.close();
            
            for(Map.Entry<String, Map<String, Integer>>entry : combinationCountMap.entrySet()){
				for(Map.Entry<String, Integer>item : entry.getValue().entrySet()){
					combination_count_writer.write(entry.getKey()+","+item.getKey()+","+item.getValue() + "\n");
				}
            }
            combination_count_writer.close();
        }catch(FileNotFoundException ex){
            ex.printStackTrace();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static Map<String, Map<String, String>> buildTagMap(String file) {
        Map<String, Map<String, String>> tagMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String str;
            while ((str=br.readLine()) != null) {
                // do something with each line
                String[] row = str.split(",");
                String dstport = row[0];
                String protocol = row[1];
                String tag = row[2];
                if(!tagMap.containsKey(protocol)){
                    tagMap.put(protocol, new HashMap<>());
                }
                tagMap.get(protocol).put(dstport, tag);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return tagMap;
    }
}