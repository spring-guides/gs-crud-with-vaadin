package hello;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.then;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.annotation.PostConstruct;

@RunWith(MockitoJUnitRunner.class)
public class CustomerEditorTests {

	private static final String FIRST_NAME = "Marcin";
	private static final String LAST_NAME = "Grzejszczak";

	@Mock CustomerRepository customerRepository;
	@InjectMocks CustomerEditor editor;
	@Mock CustomerEditor.ChangeHandler changeHandler;

	@Before
	public void init() {
		editor.setChangeHandler(changeHandler);
	}

	@Test
	public void shouldStoreCustomerInRepoWhenEditorSaveClicked() {
		emptyCustomerWasSetToForm();

		this.editor.firstName.setValue(FIRST_NAME);
		this.editor.lastName.setValue(LAST_NAME);

		this.editor.save();

		then(this.customerRepository).should().save(argThat(customerMatchesEditorFields()));
	}

	@Test
	public void shouldDeleteCustomerFromRepoWhenEditorDeleteClicked() {
		customerDataWasFilled();

		editor.delete();

		then(this.customerRepository).should().delete(argThat(customerMatchesEditorFields()));
	}

	private void emptyCustomerWasSetToForm() {
		this.editor.editCustomer(new Customer());
	}
	private void customerDataWasFilled() {
		this.editor.editCustomer(new Customer(FIRST_NAME, LAST_NAME));
	}

	private ArgumentMatcher<Customer> customerMatchesEditorFields() {
		return customer -> FIRST_NAME.equals(customer.getFirstName()) && LAST_NAME.equals(customer.getLastName());
	}

}
