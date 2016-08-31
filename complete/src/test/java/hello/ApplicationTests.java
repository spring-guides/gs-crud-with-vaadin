package hello;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.then;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ApplicationTests {

    @Autowired
    private CustomerRepository repository;

    @Test
    public void shouldFillOutComponentsWithDataWhenTheApplicationIsStarted() {
        then(this.repository.count()).isEqualTo(5);
    }

    @Test
    public void shouldFindTwoBauerCustomers() {
        then(this.repository.findByLastNameStartsWithIgnoreCase("Bauer")).hasSize(2);
    }
}
