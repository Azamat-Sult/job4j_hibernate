package ru.job4j.hql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

public class HQLUsage {
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try {
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();

            Query query = session.createQuery("from Candidate");
            for (Object candidate : query.list()) {
                System.out.println(candidate);
            }

            query = session.createQuery("from Candidate c where c.id = 2");
            System.out.println(query.uniqueResult());

            query = session.createQuery("from Candidate c where c.name = 'Senior'");
            for (Object senior : query.list()) {
                System.out.println(senior);
            }

            query = session.createQuery(
                    "update Candidate c set c.name = :newName, c.salary = :newSalary where c.id = :id"
            );
            query.setParameter("newName", "Junior +");
            query.setParameter("newSalary", 100000);
            query.setParameter("id", 1);
            query.executeUpdate();

            session.createQuery("delete from Candidate where id = :id")
                    .setParameter("id", 2)
                    .executeUpdate();

            session.getTransaction().commit();
            session.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}