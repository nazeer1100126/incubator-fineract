package org.apache.fineract.infrastructure.core.domain;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.springframework.data.domain.Persistable;


@MappedSuperclass
public abstract class AbstractPersistableCustom<PK extends Serializable> implements Persistable<Long> {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        /*
         * (non-Javadoc)
         * 
         * @see org.springframework.data.domain.Persistable#getId()
         */
        @Override
        public Long getId() {
                return id;
        }

        /**
         * Sets the id of the entity.
         * 
         * @param id the id to set
         */
        protected void setId(final Long id) {

                this.id = id;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.springframework.data.domain.Persistable#isNew()
         */
        @Override
        public boolean isNew() {

                return null == this.id;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {

                return String.format("Entity of type %s with id: %s", this.getClass().getName(), this.id);
        }

      /*  
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {

                if (null == obj) {
                        return false;
                }

                if (this == obj) {
                        return true;
                }

                if (!getClass().equals(obj.getClass())) {
                        return false;
                }

                AbstractPersistableCustom<?> that = (AbstractPersistableCustom<?>) obj;

                return null == this.getId() ? false : this.getId().equals(that.getId());
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {

                int hashCode = 17;

                hashCode += null == this.getId() ? 0 : this.getId().hashCode() * 31;

                return hashCode;
        }
}
