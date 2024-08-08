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
            Map<String, Integer> tagCountMap = new HashMap<>();
            Map<String, Map<String, Integer>> combinationCountMap = new HashMap<>(); //Use nested map to save space multiple keys

            String str;
            while ((str=br.readLine()) != null) {
				// Assume the flow log file format based on https://docs.aws.amazon.com/vpc/latest/userguide/flow-log-records.html
                String[] log = str.split("[ ,\t]+"); // assume the log field is delimited by comma
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
			
			writeToTagCountFile(tagCountMap);
			
			writeToCombinationCountFile(combinationCountMap);
			
			logger.info("FlowLogParser completed.")
        }catch(FileNotFoundException ex){
            ex.printStackTrace();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static Map<String, Map<String, String>> buildTagMap(String file) {
        Map<String, Map<String, String>> tagMap = new HashMap<>(); //Store the tagMap in a nested map to save space for multiple keys(dstport, protocol)
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String str;
            while ((str=br.readLine()) != null) {
                String[] row = str.split("[ ,\t]+");
                String dstport = row[0];
                String protocol = row[1];
                String tag = row[2];
                if(!tagMap.containsKey(protocol)){
                    tagMap.put(protocol, new HashMap<>());
                }
                tagMap.get(protocol).put(dstport, tag);
            }
		}catch(FileNotFoundException ex){
            ex.printStackTrace();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return tagMap;
    }
	
	public static void writeToTagCountFile(Map<String, Integer> tagCountMap){
		try{
			BufferedWriter tag_count_writer = new BufferedWriter(new FileWriter("tagCount.txt"));
		    tag_count_writer.write("Tag"+"\t"+"Count" + "\n");
		    for(Map.Entry<String, Integer> entry : tagCountMap.entrySet()){
                tag_count_writer.write(entry.getKey()+"\t"+entry.getValue() + "\n");
            }
            tag_count_writer.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void writeToCombinationCountFile(Map<String, Map<String, Integer>> combinationCountMap){
		try{
		    BufferedWriter combination_count_writer = new BufferedWriter(new FileWriter("combinationCount.txt"));
		    combination_count_writer.write("Port"+"\t"+"Protocol"+"\t"+ "Count" + "\n");        
            for(Map.Entry<String, Map<String, Integer>>entry : combinationCountMap.entrySet()){
			    for(Map.Entry<String, Integer>item : entry.getValue().entrySet()){
			        combination_count_writer.write(entry.getKey()+"\t"+item.getKey()+"\t"+item.getValue() + "\n");
			    }
            }
            combination_count_writer.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
