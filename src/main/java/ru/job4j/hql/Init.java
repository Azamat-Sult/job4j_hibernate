package ru.job4j.hql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class Init {
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try {
            SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Session session = sf.openSession();
            session.beginTransaction();

            JobPost post1 = JobPost.of("Post 1", "Post 1");
            JobPost post2 = JobPost.of("Post 2", "Post 2");
            JobPost post3 = JobPost.of("Post 3", "Post 3");
            JobPost post4 = JobPost.of("Post 4", "Post 4");

            PostDB postDB = PostDB.of("PostDB 1");

            Candidate junior = Candidate.of("Junior", "No exp", 80000);
            Candidate middle = Candidate.of("Middle", "2 year exp", 150000);
            Candidate senior1 = Candidate.of("Senior", "5 year exp", 250000);
            Candidate senior2 = Candidate.of("Senior", "6 year exp", 260000);

            postDB.addJobPost(post1);
            postDB.addJobPost(post2);
            postDB.addJobPost(post3);
            postDB.addJobPost(post4);

            junior.setPostDB(postDB);
            middle.setPostDB(postDB);
            senior1.setPostDB(postDB);
            senior2.setPostDB(postDB);

            session.save(junior);
            session.save(middle);
            session.save(senior1);
            session.save(senior2);

            session.getTransaction().commit();
            session.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}