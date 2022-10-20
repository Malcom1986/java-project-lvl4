package hexlet.code.domain;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.List;

@Entity
public final class Url extends Model {
    @Id
    private long id;

    String name;

    @WhenCreated
    Instant createdAt;

    @OneToMany(cascade = CascadeType.ALL)
    private List<UrlCheck> checks;

    public Url(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return  createdAt;
    }

    public List<UrlCheck> getChecks() {
        return checks;
    }
}
