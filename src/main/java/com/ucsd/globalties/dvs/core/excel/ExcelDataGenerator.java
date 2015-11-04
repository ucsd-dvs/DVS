package com.ucsd.globalties.dvs.core.excel;

import com.ucsd.globalties.dvs.core.Main;
import com.ucsd.globalties.dvs.core.Patient;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

//
//

/**
 * Basic excel export class that writes patient information into an excel file
 * TODO make excel file prettier with formatting and colors
 * TODO add disease detection results to excel file
 *
 * @author Sabit
 */
@Slf4j
public class ExcelDataGenerator {

    public static void writeExcel(List<Patient> patientList) throws Exception {
        if (patientList == null) {
            log.info("nothing to export");
            return;
        }

        Writer writer = null;
        try {
            File file = new File("C:\\DVS.csv.");
            writer = new BufferedWriter(new FileWriter(file));

            for (String field : Main.sceneLabels) {
                String text = field;
                writer.write(text);
            }

            for (Patient p : patientList) {
                Map<String, String> patientData = p.getPatientData();
                for (String field : Main.sceneLabels) {
                    String text = patientData.get(field);
                    writer.write(text);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
      /*finally {
         
           writer.flush();
           writer.close();
      } */
    }


    public static void exportPatientData(List<Patient> patientList) {
        if (patientList == null) {
            log.info("nothing to export");
            return;
        }
        try {
            //Slashes and colons in file name will break writing to output
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-YYYY hm");
            FileOutputStream out = new FileOutputStream(Main.OUTPUT_FILE + "DVS Data " + sdf.format(Calendar.getInstance().getTime()) + ".xlsx");
            Workbook wb = new XSSFWorkbook();
            Sheet s = wb.createSheet("Patient Data");
            int rowNum = 0, cellNum = 0;
            Row fieldRow = s.createRow(rowNum++);

            for (String field : Main.sceneLabels) {
                Cell c = fieldRow.createCell(cellNum++);
                c.setCellValue(field);
            }

            for (Patient p : patientList) {
                Row r = s.createRow(rowNum++);
                cellNum = 0;
                Map<String, String> patientData = p.getPatientData();
                for (String field : Main.sceneLabels) {
                    Cell c = r.createCell(cellNum++);
                    c.setCellValue(patientData.get(field));
                }
            }
            wb.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
