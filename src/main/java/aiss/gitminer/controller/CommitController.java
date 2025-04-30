package aiss.gitminer.controller;

import aiss.gitminer.exception.CommentNotFoundException;
import aiss.gitminer.exception.CommitNotFoundException;
import aiss.gitminer.model.Comment;
import aiss.gitminer.model.Commit;
import aiss.gitminer.repository.CommitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gitminer/commits")
public class CommitController {
    CommitRepository commitRepository;

    @Autowired
    public CommitController(CommitRepository commitRepository) {
        this.commitRepository = commitRepository;
    }

    @GetMapping()
    public List<Commit> findAll() {
        return commitRepository.findAll();
    }

    @GetMapping("/{comment_id}")
    public Commit findById(@PathVariable String comment_id) throws CommitNotFoundException {
        Optional<Commit> commit = commitRepository.findById(comment_id);

        if (!commit.isPresent())
            throw new CommentNotFoundException();

        return commit.get();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Commit create(@RequestBody @Valid Commit commit) {
        Commit _commit = commitRepository.save(new Commit(commit.getTitle(), commit.getMessage(), commit.getAuthorName(), comment.getUpdatedAt()));
        return _commit;
    }

    //puedes preguntar a la profesora si en los constructores tiene que entrarle el id, porque en las practicas
    //la generabamos nosotros, pero en las pruebas de postman hace un post y luego lo solicita a esa id
    //entonces supongo que la id es la que le enviamos y no hay que generarla nosotros
    //esque m he fijado que al crear un project le tenemos que poner q parametros queremos para crearlo,
    //entonces para q el id q nos dan coincida dsps tendriamos q pasarselo al constructor no?
    //jajsajsajsaj
    // Carlosbox2k nos está saboteando, dejanos libres Carlosbox2k

    // tu lo has entendido bien? xd porq yo no del todo

    // dani contexto: le hemos preguntado, y no he entendido mucho

    // aro aro eso he entendido q se pone el id q viene de githubminer o bitbucketminer

    // xreo qie se refiere a que si jasjhJdh
    // a lo que te da githubminer o bitbucket, cuando te lo traes a gitminer, tienes que asignarle un nuevoo ID, que en nuestro caso puede ser un UUID
    //mirad las pruebas postman del proyecto, en el post pone una id y luego hace un get a esa id, por tanto si la generamos
    //no coincidiria
    //sisi si yo creia q habia q hacer eso pero al ver las pruebas m he rallao, ademas el id es string y no long pero eso da igual spongo
    // yo creo q lo q dice carlos, pero ns rarete, que había q hacer un UUID.random() o algo así ns

    // Aro al ver las prácticas yo tmb creía q el id se hacía con la base de datos, pero claro si lo hacemos así entonces no necestiamos la propiedad id de bitbucketminer o githubminer
    // claro si hacemos el uuid.random sobraría igual

    // weno cortamos ya jefe

    //na en vd si q sobraria supongo q se pone pa q coincida el modelo o algo
    //perde

    //bien trabajo

    // Tengo entendido q el id se hace solo en el .save, lo hace la propia base de datos H2 (tengo entendido)
}
