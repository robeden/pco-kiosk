package net.stratfordpark.pco;

import com.squareup.moshi.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.reflect.Type;
import java.util.Set;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 *
 */
public final class EnvelopeJsonAdapter extends JsonAdapter<Object> {
	public static final JsonAdapter.Factory FACTORY = new Factory() {
		@Override
		public JsonAdapter<?> create(
			Type type, Set<? extends Annotation> annotations, Moshi moshi ) {
			Set<? extends Annotation> delegateAnnotations =
				Types.nextAnnotations( annotations, Enveloped.class );
			if ( delegateAnnotations == null ) {
				return null;
			}
			Type envelope =
				Types.newParameterizedTypeWithOwner( EnvelopeJsonAdapter.class,
					Envelope.class, type );
			JsonAdapter<Envelope<?>> delegate =
				moshi.nextAdapter( this, envelope, delegateAnnotations );
			return new EnvelopeJsonAdapter( delegate );
		}
	};

	@Retention( RUNTIME )
	@JsonQualifier
	public @interface Enveloped {
	}

	private static final class Envelope<T> {
		final T data;



		Envelope( T data ) {
			this.data = data;
		}
	}

	private final JsonAdapter<Envelope<?>> delegate;



	EnvelopeJsonAdapter( JsonAdapter<Envelope<?>> delegate ) {
		this.delegate = delegate;
	}



	@Override public Object fromJson( JsonReader reader ) throws IOException {
		return delegate.fromJson( reader ).data;
	}



	@Override public void toJson( JsonWriter writer, Object value ) throws IOException {
		delegate.toJson( writer, new Envelope<>( value ) );
	}
}
