package filestorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.gridfs.GridFSDBFile;

@Controller
public class FileStorageController {
	@Autowired
	private GridFsTemplate gridFsTemplate;
	
    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }
    
    @RequestMapping(value="/list", method=RequestMethod.GET)
    public @ResponseBody List<File> listFiles() {
		List<GridFSDBFile> list = gridFsTemplate.find(null);
		List<File> listFiles = new ArrayList<File>();
		for (GridFSDBFile gridFSDBFile : list) {
			listFiles.add(convertToFile(gridFSDBFile));
		}
		return listFiles;
	}
    
    private File convertToFile(GridFSDBFile file){
    	return new File(file.getFilename());
    }
    
    @RequestMapping(value="/file", method=RequestMethod.GET)
    public void handleFileDownload(@RequestParam("name") String name, HttpServletRequest request, HttpServletResponse response){
    	GridFSDBFile file = gridFsTemplate.findOne(new Query().addCriteria(Criteria.where("filename").is(name)));
        if (file != null) {
            try {
                response.setContentType(file.getContentType());
                response.setContentLength((new Long(file.getLength()).intValue()));
                response.setHeader("content-Disposition", "attachment; filename=" + file.getFilename());// "attachment;filename=test.xls"
                // copy it to response's OutputStream
                IOUtils.copyLarge(file.getInputStream(), response.getOutputStream());
            } catch (IOException ex) {
                //_logger.info("Error writing file to output stream. Filename was '" + id + "'");
                //throw new RuntimeException("IOError writing file to output stream");
            }
        }
    	
    	
    	
    	//return (MultipartFile) file;
    }
    
    
    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(@RequestParam("name") String name, 
            @RequestParam("file") MultipartFile file){
        if (!file.isEmpty()) {
            try {
            	gridFsTemplate.store(file.getInputStream(), name/*, "image/png"/*, metaData*/);
                return "You successfully uploaded " + name + "!";
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + name + " because the file was empty.";
        }
    }
    
}
