import { Fragment } from "react";
import { Link } from "react-router-dom";
import "./breadcrumbs.css";

export type BreadcrumbItem = {
  label: string;
  to?: string;
};

type BreadcrumbsProps = {
  items: BreadcrumbItem[];
};

export default function Breadcrumbs({ items }: BreadcrumbsProps) {
  return (
    <nav className="breadcrumbs" aria-label="Breadcrumb">
      <ol className="breadcrumbs__list">
        {items.map((item, index) => {
          const isLast = index === items.length - 1;

          return (
            <Fragment key={`${item.label}-${index}`}>
              <li className="breadcrumbs__item">
                {item.to && !isLast ? (
                  <Link to={item.to} className="breadcrumbs__link">
                    {item.label}
                  </Link>
                ) : (
                  <span
                    className={
                      isLast ? "breadcrumbs__current" : "breadcrumbs__label"
                    }
                    aria-current={isLast ? "page" : undefined}
                  >
                    {item.label}
                  </span>
                )}
              </li>

              {!isLast ? (
                <li className="breadcrumbs__separator" aria-hidden="true">
                  /
                </li>
              ) : null}
            </Fragment>
          );
        })}
      </ol>
    </nav>
  );
}
