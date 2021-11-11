package ru.job4j.cars.filter;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import ru.job4j.cars.model.BodyType;
import ru.job4j.cars.model.CarBrand;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class AdRepository implements AutoCloseable {

    private static final class Lazy {
        private static final AdRepository INST = new AdRepository();
    }

    public static AdRepository instOf() {
        return AdRepository.Lazy.INST;
    }

    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sf = new MetadataSources(registry)
            .buildMetadata().buildSessionFactory();

    public Post addPost(Post post) {
        this.tx(
                session -> session.save(post)
        );
        return post;
    }

    public User addUser(User user) {
        this.tx(
                session -> session.save(user)
        );
        return user;
    }

    public CarBrand addCarBrand(CarBrand carBrand) {
        this.tx(
                session -> session.save(carBrand)
        );
        return carBrand;
    }

    public BodyType addBodyType(BodyType bodyType) {
        this.tx(
                session -> session.save(bodyType)
        );
        return bodyType;
    }

    public List<Post> findLastDayPosts() {
        return this.tx(
                session -> {
                    long currentTimeMillis = System.currentTimeMillis();
                    Date endTime = new Date(currentTimeMillis);
                    Date startTime = new Date(currentTimeMillis - 24 * 60 * 60 * 1000);
                    String hql = "select distinct p from Post p"
                            + " join fetch p.author"
                            + " join fetch p.carBrand"
                            + " join fetch p.bodyType"
                            + " where p.created between :start and :end";
                    Query hqlQuery = session.createQuery(hql);
                    hqlQuery.setParameter("start", startTime);
                    hqlQuery.setParameter("end", endTime);
                    return hqlQuery.list();
                }
        );
    }

    public List<Post> findPostsWithPhoto() {
        return this.tx(
                session -> session.createQuery(
                        "select distinct p from Post p"
                                + " join fetch p.author"
                                + " join fetch p.carBrand"
                                + " join fetch p.bodyType"
                                + " where p.photo != 'noPhoto.jpg' ").list()
        );
    }

    public List<Post> findPostsByCarBrand(CarBrand carBrand) {
        return this.tx(
                session -> {
                    String hql = "select distinct p from Post p"
                            + " join fetch p.author"
                            + " join fetch p.carBrand"
                            + " join fetch p.bodyType"
                            + " where p.carBrand = :carBrand";
                    Query hqlQuery = session.createQuery(hql);
                    hqlQuery.setParameter("carBrand", carBrand);
                    return hqlQuery.list();
                });
    }

    private <T> T tx(final Function<Session, T> command) {
        final Session session = sf.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public void close() throws Exception {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    public static void main(String[] args) {
        /*AdRepository adRepo = AdRepository.instOf();

        User user1 = User.of("email_1@mail.ru", "user 1", "pass 1");

        CarBrand carBrand1 = CarBrand.of("carBrand 1");
        BodyType bodyType1 = BodyType.of("bodyType 1");
        Post post1 = Post.of(carBrand1, bodyType1, "desc 1", user1);
        post1.setCreated(new Date(System.currentTimeMillis() - 1 * 12 * 60 * 60 * 1000));

        CarBrand carBrand2 = CarBrand.of("carBrand 2");
        BodyType bodyType2 = BodyType.of("bodyType 2");
        Post post2 = Post.of(carBrand2, bodyType2, "desc 2", user1);
        post2.setCreated(new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000));
        post2.setPhoto("bmw7.jpg");

        CarBrand carBrand3 = CarBrand.of("carBrand 3");
        BodyType bodyType3 = BodyType.of("bodyType 3");
        Post post3 = Post.of(carBrand3, bodyType3, "desc 3", user1);
        post3.setCreated(new Date(System.currentTimeMillis() - 1 * 12 * 60 * 60 * 1000));

        CarBrand carBrand4 = CarBrand.of("carBrand 4");
        BodyType bodyType4 = BodyType.of("bodyType 4");
        Post post4 = Post.of(carBrand4, bodyType4, "desc 4", user1);
        post4.setCreated(new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000));
        post4.setPhoto("renault.jpg");

        Post post5 = Post.of(carBrand4, bodyType4, "desc 5", user1);
        post5.setCreated(new Date(System.currentTimeMillis() - 4 * 24 * 60 * 60 * 1000));

        adRepo.addUser(user1);
        adRepo.addCarBrand(carBrand1);
        adRepo.addCarBrand(carBrand2);
        adRepo.addCarBrand(carBrand3);
        adRepo.addCarBrand(carBrand4);
        adRepo.addBodyType(bodyType1);
        adRepo.addBodyType(bodyType2);
        adRepo.addBodyType(bodyType3);
        adRepo.addBodyType(bodyType4);
        adRepo.addPost(post1);
        adRepo.addPost(post2);
        adRepo.addPost(post3);
        adRepo.addPost(post4);
        adRepo.addPost(post5);

        System.out.println("Last day posts:");
        for (Post post : adRepo.findLastDayPosts()) {
            System.out.println(post);
        }

        System.out.println("Posts with photos:");
        for (Post post : adRepo.findPostsWithPhoto()) {
            System.out.println(post);
        }

        carBrand4.setId(4);
        System.out.println("Posts of 'carBrand 4':");
        for (Post post : adRepo.findPostsByCarBrand(carBrand4)) {
            System.out.println(post);
        }*/

    }

}