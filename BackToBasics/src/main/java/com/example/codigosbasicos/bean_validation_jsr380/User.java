package com.example.codigosbasicos.bean_validation_jsr380;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
 
/**
 All of the annotations used in the example are standard JSR annotations:

    @NotNull – validates that the annotated property value is not null
    @AssertTrue – validates that the annotated property value is true
    @Size – validates that the annotated property value has a size between the attributes min and max; can be applied to String, Collection, Map, and array properties
    @Min – vValidates that the annotated property has a value no smaller than the value attribute
    @Max – validates that the annotated property has a value no larger than the value attribute
    @Email – validates that the annotated property is a valid email address

Some annotations accept additional attributes, but the message attribute is common to all of them. This is the message that will usually be rendered when the value of the respective property fails validation.

Some additional annotations that can be found in the JSR are:

    @NotEmpty – validates that the property is not null or empty; can be applied to String, Collection, Map or Array values
    @NotBlank – can be applied only to text values and validated that the property is not null or whitespace
    @Positive and @PositiveOrZero – apply to numeric values and validate that they are strictly positive, or positive including 0
    @Negative and @NegativeOrZero – apply to numeric values and validate that they are strictly negative, or negative including 0
    @Past and @PastOrPresent – validate that a date value is in the past or the past including the present; can be applied to date types including those added in Java 8
    @Future and @FutureOrPresent – validates that a date value is in the future, or in the future including the present

The validation annotations can also be applied to elements of a collection:
	List<@NotBlank String> preferences;
	
The specification also supports the new Optional type in Java 8:

	private LocalDate dateOfBirth;
	public Optional<@Past LocalDate> getDateOfBirth() {
	    return Optional.of(dateOfBirth);
	}
 * */

public class User {
 
    @NotNull(message = "Name cannot be null")
    private String name;
 
    @AssertTrue
    private boolean working;
 
    @Size(min = 10, max = 200, message = "About Me must be between 10 and 200 characters")
    private String aboutMe;
 
    @Min(value = 18, message = "Age should not be less than 18")
    @Max(value = 150, message = "Age should not be greater than 150")
    private int age;
 
    @Email(message = "Email should be valid")
    private String email;

    
	public String getName() {
		return name;
	}

	public boolean isWorking() {
		return working;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public int getAge() {
		return age;
	}

	public String getEmail() {
		return email;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWorking(boolean working) {
		this.working = working;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setEmail(String email) {
		this.email = email;
	}
 
}
