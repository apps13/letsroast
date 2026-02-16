from fastapi import FastAPI

from app.db.session import engine
from app.db.models import Base

app = FastAPI(title="LetsRoast")


@app.on_event("startup")
def on_startup():
    Base.metadata.create_all(bind=engine)


@app.get("/health")
def health():
    return {"status": "ok"}
