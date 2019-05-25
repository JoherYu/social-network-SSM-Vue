import com.github.javafaker.Faker;

public class TestTagMapper {


    public static void main(String[] args) throws Exception {
        Faker faker = new Faker();
        System.out.println(faker.name().firstName());
        System.out.println(faker.name().name());
        System.out.println(faker.internet().emailAddress());
        System.out.println(faker.internet().domainName());
        System.out.println(faker.address().city());
        System.out.println(faker.food().ingredient());
        System.out.println(faker.shakespeare().romeoAndJulietQuote());
        System.out.println(faker.lorem().paragraph());
    }

}
