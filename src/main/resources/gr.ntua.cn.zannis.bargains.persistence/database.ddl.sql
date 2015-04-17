/* create products table sequence */
CREATE SEQUENCE public.prod_seq START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
/* create shops table sequence */
CREATE SEQUENCE public.shop_seq START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
/* create categories table sequence */
CREATE SEQUENCE public.category_seq START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
/* create prices table sequence */
CREATE SEQUENCE public.price_seq START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
/* create skus table sequence */
CREATE SEQUENCE public.sku_seq START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
/* create manufacturers table sequence */
CREATE SEQUENCE public.manufacturer_seq START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
/* create requests table sequence */
CREATE SEQUENCE public.request_seq START 1 INCREMENT 1 NO MAXVALUE CACHE 1;


/* categories table */
CREATE TABLE IF NOT EXISTS public.categories
(
  id          INT PRIMARY KEY       NOT NULL DEFAULT nextval('category_seq'),
  skroutz_id  INT UNIQUE            NOT NULL, -- the sku id we get from a request to the Skroutz API
  name        VARCHAR(100)          NOT NULL, -- the category name
  image_url   VARCHAR(300), -- the category's image url
  parent_id   INT REFERENCES categories (skroutz_id), -- the parent id
  etag        VARCHAR(32), -- a tag used for conditional http requests
  inserted_at TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP, -- the timestamp when the item was inserted
  modified_at TIMESTAMP, -- the timestamp when the item was last modified
  checked_at  TIMESTAMP             NOT NULL  -- the timestamp when we last queried the given item
);

/* manufacturers table */
CREATE TABLE IF NOT EXISTS public.manufacturers
(
  id          INT PRIMARY KEY  NOT NULL DEFAULT nextval('manufacturer_seq'),
  skroutz_id  INT UNIQUE       NOT NULL, -- the manufacturer id we get from a request to the Skroutz API
  name        VARCHAR(100)     NOT NULL, -- the manufacturer's name
  image_url   VARCHAR(300), -- the manufacturer's image url
  etag        VARCHAR(32), -- a tag used for conditional http requests
  inserted_at TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP, -- the timestamp when the item was inserted
  modified_at TIMESTAMP, -- the timestamp when the item was last modified
  checked_at  TIMESTAMP           -- the timestamp when we last queried the given item
);

/* shops table, some of the Skroutz fields were omitted because they were
 * excessive for our needs.
 */
CREATE TABLE IF NOT EXISTS public.shops
(
  id            INT PRIMARY KEY  NOT NULL DEFAULT nextval('shop_seq'),
  skroutz_id    INT UNIQUE       NOT NULL, -- the shop id we get from a request to the Skroutz API
  name          VARCHAR(100)     NOT NULL, -- the shop name
  link          VARCHAR(300)     NOT NULL, -- the shop url
  phone         VARCHAR(20), -- the shop's phone, a 10 digit number
  image_url     VARCHAR(300), -- the shop's image url
  thumbshot_url VARCHAR(300), -- the shop's thumbnail url
  etag          VARCHAR(32), -- a tag used for conditional http requests
  inserted_at   TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP, -- the timestamp when the item was inserted
  modified_at   TIMESTAMP, -- the timestamp when the item was last modified
  checked_at    TIMESTAMP            -- the timestamp when we last queried the given item
);

/* skus table */
CREATE TABLE IF NOT EXISTS public.skus
(
  id           INT PRIMARY KEY NOT NULL DEFAULT nextval('sku_seq'),
  skroutz_id   INT UNIQUE      NOT NULL, -- the sku id we get from a request to the Skroutz API
  name         VARCHAR(100)    NOT NULL, -- the sku name
--   ean          VARCHAR(100)       NOT NULL, -- UNNEEDED
--   pn           VARCHAR(100)       NOT NULL, -- part number UNNEEDED
  display_name VARCHAR(100)    NOT NULL, -- the sku display name
  category_id  INT             NOT NULL REFERENCES categories (skroutz_id), -- the category id
  click_url    VARCHAR(300), -- the sku's url
  price_max    NUMERIC(5, 2)   NOT NULL, -- the max price the sku had at the last check
  price_min    NUMERIC(5, 2)   NOT NULL, -- the min price the sku had at the last check
  etag         VARCHAR(32), -- a tag used for conditional http requests
  inserted_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP, -- the timestamp when the item was inserted
  modified_at  TIMESTAMP, -- the timestamp when the item was last modified
  checked_at   TIMESTAMP    -- the timestamp when we last queried the given item
);

/* products table */
CREATE TABLE IF NOT EXISTS public.products
(
  id                 INT PRIMARY KEY      NOT NULL DEFAULT nextval('prod_seq'),
  skroutz_id         INT UNIQUE           NOT NULL, -- the product id we get from a request to the Skroutz API
  name               VARCHAR(100)         NOT NULL, -- the product name
  sku_id             INT                  NOT NULL REFERENCES skus (skroutz_id), -- the product code given to match this product in other shops
  shop_id            INT                  NOT NULL REFERENCES shops (skroutz_id),
  shop_uid           VARCHAR(30), -- the unique uid given from the shop
  category_id        INT                  NOT NULL REFERENCES categories (skroutz_id),
  etag               VARCHAR(32), -- a tag used for conditional http requests
  availability       VARCHAR(50), -- the current availability
  click_url          VARCHAR(300), -- the url given from Skroutz API
  price              NUMERIC(5, 2)        NOT NULL, -- the current price
  price_changes      INT                  NOT NULL DEFAULT 0, -- how many times the product's price has changed
  average_past_price NUMERIC(5, 2)        NOT NULL, -- the average price we have calculated for the product in the past
  is_bargain         BOOL, -- true if the product is a bargain
  inserted_at        TIMESTAMP            NOT NULL DEFAULT CURRENT_TIMESTAMP, -- the timestamp when the item was inserted
  modified_at        TIMESTAMP, -- the timestamp when the item was last modified
  checked_at         TIMESTAMP      -- the timestamp when we last queried the given product
);

/* price history table */
CREATE TABLE IF NOT EXISTS public.prices
(
  id         INT PRIMARY KEY NOT NULL DEFAULT nextval('price_seq'),
  product_id INT             NOT NULL REFERENCES products (id), -- the product id
  price      NUMERIC(5, 2)   NOT NULL, -- the price
  checked_at TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP -- the moment we logged the change
);

CREATE TABLE IF NOT EXISTS public.requests
(
  id          INT PRIMARY KEY     NOT NULL DEFAULT nextval('request_seq'),
  request_uri VARCHAR(200) UNIQUE NOT NULL, -- the request uri on the skroutz website
  etag        VARCHAR(32), -- the etag we received on the last request
  successful      BOOLEAN NOT NULL, -- the result of the request
  checked_at  TIMESTAMP     -- the timestamp when we last hit the selected request
);

-- match sequences to their tables
ALTER SEQUENCE public.prod_seq OWNED BY products.id;
ALTER SEQUENCE public.shop_seq OWNED BY shops.id;
ALTER SEQUENCE public.category_seq OWNED BY categories.id;
ALTER SEQUENCE public.price_seq OWNED BY prices.id;
ALTER SEQUENCE public.sku_seq OWNED BY skus.id;
ALTER SEQUENCE public.manufacturer_seq OWNED BY manufacturers.id;
ALTER SEQUENCE public.request_seq OWNED BY requests.id;

-- create indexes on skroutz_id on all entity tables
CREATE INDEX skroutz_index_category ON categories (skroutz_id);
CREATE INDEX skroutz_index_manufacturer ON manufacturers (skroutz_id);
CREATE INDEX skroutz_index_shop ON shops (skroutz_id);
CREATE INDEX skroutz_index_sku ON skus (skroutz_id);
CREATE INDEX skroutz_index_product ON products (skroutz_id);

