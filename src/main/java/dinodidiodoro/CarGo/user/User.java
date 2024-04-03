package dinodidiodoro.CarGo.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import dinodidiodoro.CarGo.bookings.Booking;
import dinodidiodoro.CarGo.payment.Payment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@SuppressWarnings("serial")
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({ "password", "accountNonExpired", "authorities", "credentialsNonExpired", "accountNonLocked" })
public class User implements UserDetails {
	@Id
	@GeneratedValue
	private UUID id;
	private String name;
	private String surname;
	@Column(nullable = false, unique = true)
	private String email;
	private String password;
	@Enumerated(EnumType.STRING)
	private Role role;
	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.DETACH )
	@JsonManagedReference
	private List<Booking> bookings = new ArrayList<>();
	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JsonManagedReference
	private List<Payment> payments = new ArrayList<>();

	public User(String name, String surname, String email, String password) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.role = Role.USER;
		
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}