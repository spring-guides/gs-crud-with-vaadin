package hello;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.*;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.boot.VaadinAutoConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = VaadinUITests.Config.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class VaadinUITests {

	@Autowired CustomerRepository repository;

	VaadinRequest vaadinRequest = Mockito.mock(VaadinRequest.class);

	CustomerEditor editor;

	VaadinUI vaadinUI;

	@Before
	public void setup() {
		this.editor = new CustomerEditor(this.repository);
		this.vaadinUI = new VaadinUI(this.repository, editor);
	}

	@Test
	public void shouldInitializeTheGridWithCustomerRepositoryData() {
		int customerCount = (int) this.repository.count();

		vaadinUI.init(this.vaadinRequest);

		then(vaadinUI.grid.getColumns()).hasSize(3);
		then(getCustomersInGrid()).hasSize(customerCount);
	}

	private List<Customer> getCustomersInGrid() {
		ListDataProvider<Customer> ldp = (ListDataProvider) vaadinUI.grid.getDataProvider();
		return new ArrayList<>(ldp.getItems());
	}

	@Test
	public void shouldFillOutTheGridWithNewData() {
		int initialCustomerCount = (int) this.repository.count();
		this.vaadinUI.init(this.vaadinRequest);
		customerDataWasFilled(editor, "Marcin", "Grzejszczak");

		this.editor.save.click();

		then(getCustomersInGrid()).hasSize(initialCustomerCount + 1);

		then(getCustomersInGrid().get(getCustomersInGrid().size() - 1))
			.extracting("firstName", "lastName")
			.containsExactly("Marcin", "Grzejszczak");

	}

	@Test
	public void shouldFilterOutTheGridWithTheProvidedLastName() {
		this.vaadinUI.init(this.vaadinRequest);
		this.repository.save(new Customer("Josh", "Long"));

		vaadinUI.listCustomers("Long");

		then(getCustomersInGrid()).hasSize(1);
		then(getCustomersInGrid().get(getCustomersInGrid().size() - 1))
			.extracting("firstName", "lastName")
			.containsExactly("Josh", "Long");
	}

	@Test
	public void shouldInitializeWithInvisibleEditor() {
		this.vaadinUI.init(this.vaadinRequest);

		then(this.editor.isVisible()).isFalse();
	}

	@Test
	public void shouldMakeEditorVisible() {
		this.vaadinUI.init(this.vaadinRequest);
		Customer first = getCustomersInGrid().get(0);
		this.vaadinUI.grid.select(first);

		then(this.editor.isVisible()).isTrue();
	}

	private void customerDataWasFilled(CustomerEditor editor, String firstName,
			String lastName) {
		this.editor.firstName.setValue(firstName);
		this.editor.lastName.setValue(lastName);
		editor.editCustomer(new Customer(firstName, lastName));
	}

	@Configuration
	@EnableAutoConfiguration(exclude = VaadinAutoConfiguration.class)
	static class Config {

		@Autowired
		CustomerRepository repository;

		@PostConstruct
		public void initializeData() {
			this.repository.save(new Customer("Jack", "Bauer"));
			this.repository.save(new Customer("Chloe", "O'Brian"));
			this.repository.save(new Customer("Kim", "Bauer"));
			this.repository.save(new Customer("David", "Palmer"));
			this.repository.save(new Customer("Michelle", "Dessler"));
		}
	}
}
