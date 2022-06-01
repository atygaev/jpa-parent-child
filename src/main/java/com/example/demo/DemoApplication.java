package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import java.util.List;

@MappedSuperclass
class Parent {

    private String field;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}

@Entity
@Table(name = "childs")
class Child extends Parent {

    @Id
    @GeneratedValue
    private Integer id;

    private String additionalField;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAdditionalField() {
        return additionalField;
    }

    public void setAdditionalField(String additionalField) {
        this.additionalField = additionalField;
    }

    @Override
    public String toString() {
        return "Child{" +
                "id=" + id +
                ", additionalField='" + additionalField + '\'' +
                ", field=" + getField() +
                '}';
    }
}

interface ChildRepository extends JpaRepository<Child, Integer> {

    List<Child> findByField(String field);
}

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ChildRepository repository;

    @Autowired
    private EntityManager entityManager;

    @PostConstruct
    public void init() {
        transactionTemplate.executeWithoutResult($ -> {
            Child child = new Child();
            child.setField("true");
            child.setAdditionalField("field==true");
            repository.save(child);
        });

        transactionTemplate.executeWithoutResult($ -> {
            Child child = new Child();
            child.setField("false");
            child.setAdditionalField("field==false");
            repository.save(child);
        });

        transactionTemplate.executeWithoutResult($ -> {
            System.out.println(repository.findAll());
        });

        transactionTemplate.executeWithoutResult($ -> {
            List list = entityManager.createQuery("select c from Child c where c.field = :field")
                    .setParameter("field", "true")
                    .getResultList();

            System.out.println(list);

            System.out.println(repository.findByField("true"));
            System.out.println(repository.findByField("false"));
        });
    }
}
