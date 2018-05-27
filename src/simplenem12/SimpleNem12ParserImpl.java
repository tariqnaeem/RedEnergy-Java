/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplenem12;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.ArrayList;
import simplenem12.MeterRead;
import simplenem12.EnergyUnit;
import simplenem12.MeterVolume;
import simplenem12.Quality;
import java.util.Scanner;
import java.util.SortedMap;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
/**
 *
 * @author miamediawork2
 */
public class SimpleNem12ParserImpl implements SimpleNem12Parser {
    
    private MeterRead mr = null; 
    private Collection<MeterRead> meterReadings = new ArrayList<>();
    
    @Override
    public Collection<MeterRead> parseSimpleNem12(File simpleNem12File) {
        try{
            
            //get the input file
            Scanner inputFile = new Scanner(simpleNem12File);
            //specifying the date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        
            while(inputFile.hasNext()){
            
                String newLine = inputFile.nextLine(); //get new line
                String[] arr = newLine.split(","); //split into relevant columns    
                int recordType = Integer.parseInt(arr[0]);//get record type from first value 100,200,300,900
            
                if(recordType == 200 || recordType == 900){ //either intiate MeterRecord object or end of file
                    if(mr !=null){
                        meterReadings.add(mr);
                    }
                    if(recordType == 200){ //initialize new meter record
                        mr = new MeterRead(arr[1], EnergyUnit.valueOf(arr[2]));
                    } 
                } else if (recordType == 300){//intialize new meter volume against each meter record
                    MeterVolume mv = new MeterVolume(new BigDecimal(arr[2]),Quality.valueOf(arr[3]));
                    SortedMap<LocalDate, MeterVolume> sm = new TreeMap<>();
                    sm.put(LocalDate.parse(arr[1], formatter), mv);
                    mr.setVolumes(sm);
                }
        }
        inputFile.close();
        
        return meterReadings;
        } catch(FileNotFoundException e){
             e.printStackTrace();
             return null;
        }
        
    }
}
