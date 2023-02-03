package it.unimib.sal.one_two_trip.data.database.model.response;

/**
 * Class to represent the API response of the Unsplash photo service.
 * <a href="https://unsplash.com/">See docs</a>
 */
public class PictureApiResponse {

    private int total;
    private int total_pages;
    private PictureApiResult[] results;
    private String[] errors;

    public PictureApiResponse() {
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public PictureApiResult[] getResults() {
        return results;
    }

    public void setResults(PictureApiResult[] results) {
        this.results = results;
    }

    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }

    public static class PictureApiResult {
        private Urls urls;

        public PictureApiResult() {
        }

        public Urls getUrls() {
            return urls;
        }

        public void setUrls(Urls urls) {
            this.urls = urls;
        }

        public static class Urls {
            private String regular;

            public Urls() {
            }

            public String getRegular() {
                return regular;
            }

            public void setRegular(String regular) {
                this.regular = regular;
            }
        }
    }
}
