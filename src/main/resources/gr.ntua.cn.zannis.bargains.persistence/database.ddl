/* create products table sequence */
CREATE SEQUENCE public.prod_seq START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
/* create shops table sequence */
CREATE SEQUENCE public.shop_seq START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
/* create categories table sequence */
CREATE SEQUENCE public.categ_seq START 1 INCREMENT 1 NO MAXVALUE CACHE 1;
/* create categories table sequence */
CREATE SEQUENCE public.price_seq START 1 INCREMENT 1 NO MAXVALUE CACHE 1;

/* products table */
CREATE TABLE IF NOT EXISTS public.products
(
  id                 SERIAL PRIMARY KEY NOT NULL DEFAULT nextval('prod_seq'),
  skroutz_id         INT                NOT NULL, -- the product id we get from a request to the Skroutz API
  name               VARCHAR(100)       NOT NULL, -- the product name
  sku_id             VARCHAR(30)        NOT NULL, -- the product code given from the current shop
  shop_id            INT                NOT NULL REFERENCES shops (id),
  category_id        INT                NOT NULL REFERENCES categories (id),
  etag               VARCHAR(32),       -- a tag used for conditional http requests
  availability       VARCHAR(50),       -- the current availability
  click_url          VARCHAR(300),      -- the url given from Skroutz API
  price_id           NUMERIC(5, 2),     -- the current price
  price_changes      INT                NOT NULL DEFAULT 0, -- how many times the product's price has changed
  average_past_price NUMERIC(5, 2)      NOT NULL, -- the average price we have calculated for the product in the past
  inserted_at        TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified_at        TIMESTAMP,         -- the timestamp when the item's price was last modified
  checked_at         TIMESTAMP          -- the timestamp when we last queried the given product
);

/* shops table */
CREATE TABLE IF NOT EXISTS public.shops
(
  id         SERIAL PRIMARY KEY NOT NULL DEFAULT nextval('shop_seq'),
  skroutz_id INT                NOT NULL, -- the shop id we get from a request to the Skroutz API
  name       VARCHAR(100)       NOT NULL -- the shop name

);

/* categories table */
CREATE TABLE IF NOT EXISTS public.categories
(
  id         SERIAL PRIMARY KEY NOT NULL DEFAULT nextval('categ_seq'),
  skroutz_id INT                NOT NULL, -- the shop id we get from a request to the Skroutz API
  name       VARCHAR(100)       NOT NULL -- the shop name

);

/* price history table */
CREATE TABLE IF NOT EXISTS public.prices
(
  id          SERIAL PRIMARY KEY NOT NULL DEFAULT nextval('price_seq'),
  product_id  INT                NOT NULL REFERENCES products (id), -- the product id
  inserted_at TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP, -- the moment we logged the change
  name        VARCHAR(100)       NOT NULL -- the shop name

);

-- TODO : client log table??

-- match sequences to their tables
ALTER SEQUENCE public.prod_seq OWNED BY products.id;
ALTER SEQUENCE public.shop_seq OWNED BY shops.id;
ALTER SEQUENCE public.categ_seq OWNED BY categories.id;
ALTER SEQUENCE public.price_seq OWNED BY prices.id;


