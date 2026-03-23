package bi.dashboards;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bi/dashboards")
public class Controller {

    @Autowired
    private DashboardsRepository repository;

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping
    public List<DashboardsResponseDTO> getAll(){

        List<DashboardsResponseDTO> dashboardsList = repository.findAll().stream().map(DashboardsResponseDTO::new).toList();
        return dashboardsList;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(value = "id") Integer id){

        Optional<Dashboards> dashboards = repository.findById(id);
        if(dashboards.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        DashboardsResponseDTO dashboardsDTO = new DashboardsResponseDTO(dashboards.get());
        return  ResponseEntity.ok(dashboardsDTO);
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping
    public void saveCompras(@RequestBody DashboardsRequestDTO data){

        Dashboards dashboardsData = new Dashboards(data);
        repository.save(dashboardsData);
        return;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDashboards(@PathVariable(value = "id") Integer id, @RequestBody DashboardsRequestDTO upData){

        Optional<Dashboards> dashboards = repository.findById(id);
        if(dashboards.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }

        Dashboards dashboardsModel = dashboards.get();
        BeanUtils.copyProperties(upData, dashboardsModel);
        return  ResponseEntity.status(HttpStatus.OK).body(repository.save(dashboardsModel));
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDashboards(@PathVariable(value = "id") Integer id){

        Optional<Dashboards> dashboards = repository.findById(id);
        if(dashboards.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        }
        repository.delete(dashboards.get());
        return  ResponseEntity.status(HttpStatus.OK).body("Dashboards deleted");
    }
}
